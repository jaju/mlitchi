import jsonpickle
import mlitchi.cache

appname = 'org.msync.mlitchi'
jsonpickle.set_encoder_options('json',
                               use_decimal=True, sort_keys=True)
mlitchi.cache.init_app(appname)
