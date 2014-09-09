(function($) {

	var baseUrl = '/api';

	function defaultAjax(type, options) {
		var request = $.ajax({
			type : type,
			url : options.url,
			data : options.data,
			async : options.async,
			contentType : 'application/json;charset=UTF-8',
			dataType : 'json'
		});

		return request;
	}

	function save(endpoint, object) {
		var type = object.id ? 'PUT' : 'POST';
		var options = {
			url : baseUrl + endpoint,
			data : JSON.stringify(object),
		};

		return defaultAjax(type, options);
	}

	var api = {
		save : save
	};

	window.yawp = api;

})(jQuery);