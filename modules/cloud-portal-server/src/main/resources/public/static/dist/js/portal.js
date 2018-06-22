var autoScroll = false;
var autoScrollTimeout;

// IE polyfill for startsWith function
if (!String.prototype.startsWith) {
	String.prototype.startsWith = function(searchString, position){
		position = position || 0;
		return this.substr(position, searchString.length) === searchString;
	};
}

function resetFile() {
    document.forms[0].file.setAttribute('type', 'input');
    document.forms[0].file.setAttribute('type', 'file');
}

function startAutoScrollIframe() {
	$("#output")[0].contentWindow.scrollBy(0,10000);
	autoScrollTimeout = setTimeout("startAutoScrollIframe()", 500);	
}

function stopAutoScrollIframe() {
	clearTimeout(autoScrollTimeout);
}

$(function() {
	
	$("form :button").each(function(){
		
		var button = $(this);
		
		if($(button).attr('id') == 'cancel') {
			$(button).click(function(e){
				e.preventDefault();
				history.back();
			});
		} 
		else {
			if ($(button).attr('id') == 'auto-scroll') {
				$(button).click(function(e){
					e.preventDefault();
					if (autoScroll) {
						stopAutoScrollIframe();
						$(this).addClass('btn-danger').removeClass('btn-success');
						autoScroll = false;
					}
					else {
						startAutoScrollIframe();
						$(this).addClass('btn-success').removeClass('btn-danger');
						autoScroll = true;
					}
				});
			}
			else {
				if ($(button).attr('id') == 'close') {
					$(button).click(function(e){
						stopAutoScrollIframe();
						$('#auto-scroll').addClass('btn-danger').removeClass('btn-success');
						autoScroll = false;
					});
				}
				else {
					
					$(button).click(function(e){
						
						var buttonId = $(button).attr('id');
						
						if (typeof buttonId !== 'undefined') {
							
							e.preventDefault();
							var form = $(this).closest('form');
							var originalActionUrl = $(form).attr('action');
							
							if ($(form).valid()) {
								
								if (buttonId == "plan" || buttonId == "apply") {
									$('#myModal').modal('toggle');
								}
								
								if (buttonId == "execute") {
									var editor = ace.edit("editor");
									$('#script').val(editor.getValue());
								}
								
								var submit = true;
								
								if (buttonId.startsWith('unlink')) {
									submit = confirm('Do you really want to unlink this item?');
									if (submit) {
										$('#myModal').modal('toggle');
									}
								}
								
								if (buttonId.startsWith('destroy')) {
									submit = confirm('Do you really want to destroy this item?');
									if (submit) {
										$('#myModal').modal('toggle');
									}
								}
									
								if (submit) {
									$(form).attr('action', originalActionUrl + "/" + buttonId);
									$(form).submit();
									$(form).attr('action', originalActionUrl);
								}
							}
						}
					});
				}
			}
		}
	});
	
	$('#datatable').DataTable({
        responsive: true,
        order: [[ 1, 'desc' ]]
    });
	
	$("ul[data-nav-level='1']").show();
});