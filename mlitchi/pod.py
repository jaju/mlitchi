#!/usr/bin/env python
import sys
import json
from bcoding import bencode, bdecode
from pylaagu.babumoshai import (NSExportSpec,
                                to_pod_namespaced_format,
                                load_namespace, load_namespaces,
                                dispatch)
from pylaagu.utils import debug


def read():
    return dict(bdecode(sys.stdin.buffer))


def write(obj):
    sys.stdout.buffer.write(bencode(obj))
    sys.stdout.buffer.flush()


def main(nsexport_specs: list[NSExportSpec]):
    namespaces = load_namespaces(nsexport_specs)
    exports = [to_pod_namespaced_format(ns)
               for ns in namespaces.values()
               if ns is not None]
    while True:
        try:
            msg = read()
            op = msg.get("op")
            if op == "describe":
                write({
                    "format": "json",
                    "namespaces": exports,
                    "ops": {"shutdown": {}}
                })
            elif op == "invoke":
                var = msg.get("var")
                id = msg.get("id")
                args = json.loads(msg.get("args"))
                value = dispatch(namespaces, var, args)
                write({"status": ["done"], "id": id,
                       "value": json.dumps(value)})
            elif op == "shutdown":
                debug("Shutting down pod.")
                break
        except EOFError:
            print("EOF")
            break
        except Exception as e:
            print("Error", e)
            write({"status": ["error"], "ex-message": str(e), "id": msg.get("id")})


nsexport_specs = [
    NSExportSpec("mlitchi", "mlitchi/__init__.py", ns_name="py.mlitchi"),
    NSExportSpec("mlitchi.hfhub", "mlitchi/hfhub_helpers.py", ns_name="py.hfhub",
                 export_meta=True, fail_on_error=True),
    NSExportSpec("mlitchi.mlx", "mlitchi/mlx_helpers.py", ns_name="py.mlx",
                 export_meta=True, fail_on_error=False),
    NSExportSpec("mlitchi.spacy_helpers", "mlitchi/spacy_helpers.py",
                 ns_name="py.spacy"),
    NSExportSpec("mlitchi.transformers_helpers", "mlitchi/transformers_helpers.py",
                 ns_name="py.transformers"),
    NSExportSpec("huggingface_hub.hf_api", ns_name="py.hfhub-api",
                 export_module_imports=True)]


if __name__ == "__main__":
    if len(sys.argv) == 1:
        main(nsexport_specs)
    else:
        module = sys.argv[1]
        print(load_namespace(NSExportSpec(module)))
        sys.exit(0)
