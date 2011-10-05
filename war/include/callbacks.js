function say_hello() {
	$D('error_div').innerHTML = 'Hello World';
}

function update_status_cb(rfb, state, oldstate, msg) {
	var e_d;
	e_d = $D('error_div');
	e_d.innerHTML = 'State: ' + state;
}

function send_cad () {
	rfb.sendCtrlAltDel();
}

function disconnect () {
	window.rfb.disconnect();
}

function ungrab_keyboard () {
	window.rfb.get_keyboard().ungrab();
	window.rfb.get_mouse().ungrab();
}

function grab_keyboard () {
	window.rfb.get_keyboard().grab();
	window.rfb.get_mouse().grab();
}

function scale_view (newval) {
	window.rfb.get_display().set_scale(newval);
}

function send_cad () {
	window.rfb.sendCtrlAltDel();
}