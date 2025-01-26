import spacy
import spacy.tokens.doc

_nlp = spacy.load("en_core_web_sm")


# ChatGPT FTW

def _sentence_to_obj(sent):
    return {
        "start": sent.start,
        "end": sent.end,
        "text": sent.text
    }


def _entity_to_object(ent):
    return {
        "text": ent.text,
        "start": ent.start_char,
        "end": ent.end_char,
        "label": ent.label_
    }


def _token_to_object(token):
    return {
        "text": token.text,
        "lemma": token.lemma_,
        "pos": token.pos_,
        "tag": token.tag_,
        "dep": token.dep_,
        "shape": token.shape_,
        "is_alpha": token.is_alpha,
        "is_stop": token.is_stop,
        "head": token.head.i
    }


def doc_to_full_json(doc: spacy.tokens.doc.Doc):
    json_doc = {
        "text": doc.text,
        "tokens": [],
        "entities": [],
        "sents": []
    }

    # Serialize tokens
    for token in doc:
        json_doc["tokens"].append(_token_to_object(token))

    # Serialize entities
    for ent in doc.ents:
        json_doc["entities"].append(_entity_to_object(ent))

    # Serialize sentences
    for sent in doc.sents:
        json_doc["sents"].append(_sentence_to_obj(sent))

    return json_doc


def noun_chunks_to_json(doc: spacy.tokens.doc.Doc):
    json_doc = []
    for chunk in doc.noun_chunks:
        json_doc.append({
            "text": chunk.text,
            "root": chunk.root.text,
            "root_dep": chunk.root.dep_,
            "root_head": chunk.root.head.text
        })
    return json_doc

# Convert a spacy object to a python dictionary
def nlp(args):
    return doc_to_full_json(_nlp(args))


def nlp_noun_chunks(args):
    return noun_chunks_to_json(_nlp(args))


if __name__ == "__main__":
    print(nlp("Pune is my home town."))
