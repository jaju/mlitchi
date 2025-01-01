#!/usr/bin/env python
import os
import sys
import json
from bcoding import bencode, bdecode
from pylaagu.babumoshai import (NSExportSpec,
                                to_pod_export_format,
                                load_as_namespace, load_as_namespaces,
                                dispatch)
from pylaagu.utils import debug
import logging

logging.basicConfig(level=logging.INFO, filename="pod.log")
logger = logging.getLogger(__name__)

script_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, f"{script_dir}")
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler('pod.log')
fh.setLevel(logging.DEBUG)
logger.addHandler(fh)

logger.info("Starting up pod...")

def read():
    return dict(bdecode(sys.stdin.buffer))


def write(obj):
    sys.stdout.buffer.write(bencode(obj))
    sys.stdout.buffer.flush()


def main_loop(nsexport_specs: list[NSExportSpec]):
    namespaces = load_as_namespaces(nsexport_specs)
    # for namespace in namespaces.values():
    #     logger.info(namespace.vars)
    exports = [to_pod_export_format(ns)
               for ns in namespaces.values()
               if ns is not None]
    while True:
        try:
            msg = read()
            op = msg.get("op")
            if op == "describe":
                logger.info(exports)
                write({
                    "format": "json",
                    "namespaces": exports,
                    "ops": {"shutdown": {}}
                })
            elif op == "invoke":
                var = msg.get("var")
                id = msg.get("id")
                args = json.loads(msg.get("args"))
                logger.info(f"Invoking {var} with args {args}")
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
    NSExportSpec("mlitchi", ns_name="py.mlitchi", export_module_imports=True, export_meta=True),
    NSExportSpec("mlitchi.hfhub_helpers", ns_name="py.hfhub",
                 export_meta=True, fail_on_error=True),
    # NSExportSpec("mlitchi.mlx_helpers", ns_name="py.mlx",
    #              export_meta=True, fail_on_error=False),
    NSExportSpec("mlitchi.spacy_helpers", ns_name="py.spacy", export_meta=True),
    NSExportSpec("mlitchi.transformers_helpers", ns_name="py.transformers",
                 export_meta=True),
    NSExportSpec("huggingface_hub.hf_api", ns_name="py.hfhub-api",
                 export_module_imports=True, export_meta=False)
]


if __name__ == "__main__":
    if len(sys.argv) == 1:
        main_loop(nsexport_specs)
    else:
        module = sys.argv[1]
        print(load_as_namespace(NSExportSpec(module)))
        sys.exit(0)
