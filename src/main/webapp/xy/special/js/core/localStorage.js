/**
 * 兼容 localStorage
 */
window.LocalStorage = (function(){
    var _localStorage;

    function _LocalStorage(){
    }

    _LocalStorage.prototype.getItem = function(key){
        if(this.hasOwnProperty(key)){
            return String(this[key]);
        }
        return null;
    };

    _LocalStorage.prototype.setItem = function(key, val){
        this[key] = String(val);
    };

    _LocalStorage.prototype.removeItem = function(key){
        delete this[key];
    };

    _LocalStorage.prototype.clear = function(){
        var self = this;
        Object.keys(self).forEach(function(key){
            self[key] = undefined;
            delete self[key];
        });
    };

    _LocalStorage.prototype.key = function(i){
        i = i || 0;
        return Object.keys(this)[i];
    };

    _LocalStorage.prototype.__defineGetter__('length', function(){
        return Object.keys(this).length;
    });

    if(localStorage){
        _localStorage = localStorage;
    } else{
        _localStorage = new _LocalStorage();
    }
    return _localStorage;
    //return new _LocalStorage();
}());
