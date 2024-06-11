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


nsexport_specs = [
    NSExportSpec("mlitchi", "mlitchi/__init__.py", ns_name="py.mlitchi"),
    NSExportSpec("mlitchi.hf", "mlitchi/hf.py", ns_name="py.hf", export_meta=True),
    NSExportSpec("mlitchi.spacy_helpers", "mlitchi/spacy_helpers.py", ns_name="py.spacy"),
    NSExportSpec("huggingface_hub.hf_api", ns_name="py.hf-api", export_module_imports=True)]


def main(nsexport_specs: list[NSExportSpec] = nsexport_specs):
    namespaces = load_namespaces(nsexport_specs)
    exports = [to_pod_namespaced_format(ns)
               for ns in namespaces.values()]
    exports.append({"name": "pylaagu.babumoshai", "vars": [
        {"name": "load-namespace"}
    ]})
    debug(exports)
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
                if var == "pylaagu.babumoshai/load-namespace":
                    ns = load_namespace(NSExportSpec(*args))
                    debug(ns)
                    exports.append(to_pod_namespaced_format(ns))
                    debug(exports)
                    write({"status": ["done"], "id": id, "format": "json", "namespaces": exports})
                else:
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


if __name__ == "__main__":
    if len(sys.argv) == 1:
        main()
    else:
        module = sys.argv[1]
        print(load_namespace(NSExportSpec(module)))
        sys.exit(0)
