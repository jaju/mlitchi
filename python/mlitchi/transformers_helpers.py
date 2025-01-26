import functools
from transformers import AutoModelForCausalLM, AutoTokenizer
import torch
import jsonpickle

__device = "cpu"
model_registry = dict()
tokenizer_registry = dict()


def store_model(key, model):
    model_registry[key] = model


def get_model(key):
    return model_registry[key]


def store_tokenizer(key, tokenizer):
    tokenizer_registry[key] = tokenizer


def get_tokenizer(key):
    return tokenizer_registry[key]


@functools.cache
def get_device():
    if torch.cuda.is_available():
        __device = "cuda"
    elif torch.backends.mps.is_available():
        __device = "mps"
    else:
        __device = "cpu"
    return __device


# Protected APIs
def _load_model(model_name, device=__device):
    return AutoModelForCausalLM.from_pretrained(model_name).to(device)


def _load_tokenizer(model_name):
    return AutoTokenizer.from_pretrained(model_name)


# Public APIs
def load_model(model_name):
    "Loads a model and tokenizer from Hugging Face model hub."
    try:
        model = get_model(model_name)
        if model is None:
            model = _load_model(model_name)
            assert model is not None
            store_model(model_name, model)
        return {"status": True}
    except Exception as e:
        print(f"Error loading model {model_name}. Error: {e}")
        return {"status": False}


def load_tokenizer(model_name):
    "Loads a model and tokenizer from Hugging Face model hub."
    try:
        tokenizer = get_tokenizer(model_name)
        if tokenizer is None:
            tokenizer = _load_tokenizer(model_name)
            assert tokenizer is not None
            store_tokenizer(model_name, tokenizer)
        return {"status": True}
    except Exception as e:
        print(f"Error loading tokenizer {model_name}. Error: {e}")
        return {"status": False}
