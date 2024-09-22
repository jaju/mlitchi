import functools
from transformers import AutoModelForCausalLM, AutoTokenizer
import torch

__device = "cpu"
__model = None
__tokenizer = None


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


def _load_model_and_tokenizer(model_name, device=__device):
    model = _load_model(model_name, device)
    tokenizer = _load_tokenizer(model_name)
    return model, tokenizer


# Public APIs
def load_model_and_tokenizer(model_name):
    global __model, __tokenizer
    __model, __tokenizer = _load_model_and_tokenizer(model_name)
    return True

