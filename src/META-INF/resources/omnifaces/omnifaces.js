var OmniFaces=OmniFaces||{};
OmniFaces.Ajax=function(){var b=[];var c=function c(e){if(e.status==="success"){for(var d=0;d<b.length;d++){b[d].call(null);}b=[];}};return{addRunOnceOnSuccess:function a(d){if(typeof d==="function"){if(!b.length){jsf.ajax.addOnEvent(c);}b[b.length]=d;}else{throw new Error("OmniFaces.Ajax.addRunOnceOnSuccess: The given callback is not a function.");}}};}();
OmniFaces.Highlight={addErrorClass:function(f,a,d){for(var c=0;c<f.length;c++){var b=document.getElementById(f[c]);if(!b){var e=document.getElementsByName(f[c]);if(e&&e.length){b=e[0];}}if(b){b.className+=" "+a;if(d){b.focus();d=false;}}}}};
