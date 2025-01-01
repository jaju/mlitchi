import jsonpickle
import mlitchi.cache as cache

appname = 'org.msync.mlitchi'
jsonpickle.set_encoder_options('json', use_decimal=True, sort_keys=True)
cache.init_app(appname)
