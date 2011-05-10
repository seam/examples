(function($) {
    $.fn.CenterIt = function(B) {
        var C = { ignorechildren: true, showPopup: true };
        var D = $.extend({}, C, B);
        var E = $(this);
        if (D.showPopup) {
            E.show()
        }
        $(document).ready(function() {
            CenterItem()
        });
        $(window).resize(function() {
            CenterItem()
        });
        function CenterItem() {
            var a = 0;
            var b = 0;
            if (D.ignorechildren) {
                a = E.height();
                b = E.width()
            } else {
                var c = E.children();
                for (var i = 0; i < c.length; i++) {
                    if (c[i].style.display != 'none') {
                        a = c[i].clientHeight;
                        b = c[i].clientWidth
                    }
                }
            }
            var d = E.css("margin");
            var e = E.css("padding");
            if (d != null) {
                d = d.replace(/auto/gi, '0');
                d = d.replace(/px/gi, '');
                d = d.replace(/pt/gi, '')
            }
            var f = "";
            if (d != "" && d != null) {
                var g = e.split(' ');
                if (g.length == 1) {
                    var h = parseInt(g[0]);
                    f = new Array(h, h, h, h)
                } else if (g.length == 2) {
                    var j = parseInt(g[0]);
                    var k = parseInt(g[1]);
                    f = new Array(j, k, j, k)
                } else if (g.length == 3) {
                    var l = parseInt(g[0]);
                    var m = parseInt(g[1]);
                    var n = parseInt(g[2]);
                    f = new Array(l, m, n, m)
                } else if (g.length == 4) {
                    var l = parseInt(g[0]);
                    var m = parseInt(g[1]);
                    var o = parseInt(g[2]);
                    var p = parseInt(g[3]);
                    f = new Array(l, m, n, p)
                }
            }
            var k = 0;
            var j = 0;
            if (f != "NaN") {
                if (f.length > 0) {
                    k = f[1] + f[3];
                    j = f[0] + f[2]
                }
            }
            if (e != null) {
                e = e.replace(/auto/gi, '0');
                e = e.replace(/px/gi, '');
                e = e.replace(/pt/gi, '')
            }
            var q = "";
            if (e != "" && e != null) {
                var r = e.split(' ');
                if (r.length == 1) {
                    var s = parseInt(r[0]);
                    q = new Array(s, s, s, s)
                } else if (r.length == 2) {
                    var t = parseInt(r[0]);
                    var u = parseInt(r[1]);
                    q = new Array(t, u, t, u)
                } else if (r.length == 3) {
                    var v = parseInt(r[0]);
                    var w = parseInt(r[1]);
                    var x = parseInt(r[2]);
                    q = new Array(v, w, x, w)
                } else if (r.length == 4) {
                    var v = parseInt(r[0]);
                    var w = parseInt(r[1]);
                    var x = parseInt(r[2]);
                    var y = parseInt(r[3]);
                    q = new Array(v, w, x, y)
                }
            }
            var u = 0;
            var t = 0;
            if (q != "NaN") {
                if (q.length > 0) {
                    u = q[1] + q[3];
                    t = q[0] + q[2]
                }
            }
            if (j == "NaN" || isNaN(j)) {
                j = 0
            }
            if (t == "NaN" || isNaN(t)) {
                t = 0
            }
            var z = $(window).height();
            var A = $(window).width();
            if ($.browser.msie && document.documentMode < 7) {
                E.css("position", "absolute")
            } else {
                E.css("position", "fixed")
            }
            if (!D.ignorechildren) {
                E.css("height", a + "px");
                E.css("width", b + "px")
            }
            E.css("top", ((z - (a + j + t)) / 2) + "px");
            E.css("left", ((A - (b + k + u)) / 2) + "px");
            return this
        }

        return this
    }
})(jQuery);
