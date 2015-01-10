from google.appengine.ext import ndb

def yawp(path):
    def _get_kind(cls):
        return path

    Model = type(path, (ndb.Model,), {})
    Model._get_kind = classmethod(_get_kind)
    return Model