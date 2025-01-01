import huggingface_hub as hub
import jsonpickle
from .cache import diskcache


def url_of(model_name: str, filename: str) -> str:
    return hub.hf_hub_url(model_name, filename=filename)


@diskcache
def model_info(model_name: str, as_json=False) -> dict | str:
    """Returns model metadata from huggingface."""
    info = hub.model_info(model_name)
    if as_json:
        return jsonpickle.encode(info, indent=2, unpicklable=False)
    else:
        return info


@diskcache
def model_files(model_name: str) -> list:
    return hub.list_repo_files(model_name)


def __parse_args():
    import argparse
    parser = argparse.ArgumentParser(description="Hugging Face Model Info")
    parser.add_argument("model_name", type=str, help="Huggingface model name",
                        default="facebook/bart-large")
    parser.add_argument("--json", action="store_true", help="Output as JSON",
                        default=True)
    parser.add_argument("--files", action="store_true",
                        help="List model files")
    return parser.parse_args()


if __name__ == "__main__":
    args = __parse_args()
    model_name = args.model_name
    info = model_info(model_name, as_json=args.json)
    print(info)
    files = model_files(model_name)
    print(files)
    print(url_of(model_name, "config.json"))
