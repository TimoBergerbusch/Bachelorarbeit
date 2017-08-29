(function () {
    var buildDotRenderer = function (el) {
            var f = document.createElement('form');
            f.setAttribute('action', 'http://aprove.informatik.rwth-aachen.de/renderdot/cgi-bin/renderdot.cgi');
            f.setAttribute('method', 'post');
            f.setAttribute('target', '_blank');
            f.setAttribute('enctype', 'multipart/form-data');
            var v = document.createElement('input');
            v.setAttribute('type', 'hidden');
            v.setAttribute('name', 'dot');
            v.setAttribute('value', el.value);
            f.appendChild(v);
            var b = document.createElement('input');
            b.setAttribute('type', 'submit');
            b.setAttribute('value', 'Render Graph');
            f.appendChild(b);
            el.parentNode.insertBefore(f, el);
        },
        els = document.getElementsByTagName('textarea'),
        i,
        l = els.length,
        el;

    for (i = 0; i < l; ++i) {
        el = els[i];
        if(el.value.indexOf('digraph') != -1) {
            buildDotRenderer(el);
        }
    }
}());
