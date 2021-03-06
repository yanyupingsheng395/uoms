/*
 * Bootstrap year calendar v1.1.0
 * Created by Paul David-Sivelle
 * Licensed under the Apache License, Version 2.0
 */

!
function (a) {
    var b = function (a, b) {
        this.element = a,
        this.element.addClass("calendar"),
        this._initializeEvents(b),
        this._initializeOptions(b),
        this.setYear(this.options.startYear)
    };
    b.prototype = {
        constructor: b,
        _initializeOptions: function (b) {
            null == b && (b = []),
            this.options = {
                startYear: isNaN(parseInt(b.startYear)) ? (new Date).getFullYear() : parseInt(b.startYear),
                minDate: b.minDate instanceof Date ? b.minDate : null,
                maxDate: b.maxDate instanceof Date ? b.maxDate : null,
                language: null != b.language && null != c[b.language] ? b.language : "en",
                allowOverlap: null == b.allowOverlap || b.allowOverlap,
                displayWeekNumber: null != b.displayWeekNumber && b.displayWeekNumber,
                displayDisabledDataSource: null != b.displayDisabledDataSource && b.displayDisabledDataSource,
                displayHeader: null == b.displayHeader || b.displayHeader,
                alwaysHalfDay: null != b.alwaysHalfDay && b.alwaysHalfDay,
                enableRangeSelection: null != b.enableRangeSelection && b.enableRangeSelection,
                disabledDays: b.disabledDays instanceof Array ? b.disabledDays : [],
                disabledWeekDays: b.disabledWeekDays instanceof Array ? b.disabledWeekDays : [],
                hiddenWeekDays: b.hiddenWeekDays instanceof Array ? b.hiddenWeekDays : [],
                roundRangeLimits: null != b.roundRangeLimits && b.roundRangeLimits,
                dataSource: b.dataSource instanceof Array ? b.dataSource : [],
                style: "background" == b.style || "border" == b.style || "custom" == b.style ? b.style : "border",
                enableContextMenu: null != b.enableContextMenu && b.enableContextMenu,
                contextMenuItems: b.contextMenuItems instanceof Array ? b.contextMenuItems : [],
                customDayRenderer: a.isFunction(b.customDayRenderer) ? b.customDayRenderer : null,
                customDataSourceRenderer: a.isFunction(b.customDataSourceRenderer) ? b.customDataSourceRenderer : null,
                weekStart: isNaN(parseInt(b.weekStart)) ? null : parseInt(b.weekStart)
            },
            this._initializeDatasourceColors()
        },
        _initializeEvents: function (a) {
            null == a && (a = []),
            a.yearChanged && this.element.bind("yearChanged", a.yearChanged),
            a.renderEnd && this.element.bind("renderEnd", a.renderEnd),
            a.clickDay && this.element.bind("clickDay", a.clickDay),
            a.dayContextMenu && this.element.bind("dayContextMenu", a.dayContextMenu),
            a.selectRange && this.element.bind("selectRange", a.selectRange),
            a.mouseOnDay && this.element.bind("mouseOnDay", a.mouseOnDay),
            a.mouseOutDay && this.element.bind("mouseOutDay", a.mouseOutDay)
        },
        _initializeDatasourceColors: function () {
            for (var a = 0; a < this.options.dataSource.length; a++) null == this.options.dataSource[a].color && (this.options.dataSource[a].color = d[a % d.length])
        },
        render: function () {
            this.element.empty(),
            this.options.displayHeader && this._renderHeader(),
            this._renderBody(),
            this._renderDataSource(),
            this._applyEvents(),
            this.element.find(".months-container").fadeIn(500),
            this._triggerEvent("renderEnd", {
                currentYear: this.options.startYear
            })
        },
        _renderHeader: function () {},
        _renderBody: function () {
            var tmp = new Date();
            var currentMonth = tmp.getMonth();

            var b = a(document.createElement("div"));
            b.addClass("months-container");
            for (var d = currentMonth - 1; d < currentMonth + 3; d++) {
                var e = a(document.createElement("div"));
                e.addClass("month-container"),
                e.data("month-id", d);
                var f = new Date(this.options.startYear, d, 1),
                    g = a(document.createElement("table"));
                g.addClass("month");
                var h = a(document.createElement("thead")),
                    i = a(document.createElement("tr")),
                    j = a(document.createElement("th"));
                j.addClass("month-title"),
                j.attr("colspan", this.options.displayWeekNumber ? 8 : 7),
                j.text(c[this.options.language].months[d]),
                i.append(j),
                h.append(i);
                var k = a(document.createElement("tr"));
                if (this.options.displayWeekNumber) {
                        var l = a(document.createElement("th"));
                        l.addClass("week-number"),
                        l.text(c[this.options.language].weekShort),
                        k.append(l)
                    }
                var m = this.options.weekStart ? this.options.weekStart : c[this.options.language].weekStart,
                    n = m;
                do {
                        var o = a(document.createElement("th"));
                        o.addClass("day-header"), o.text(c[this.options.language].daysMin[n]), this._isHidden(n) && o.addClass("hidden"), k.append(o), n++, n >= 7 && (n = 0)
                    } while (n != m);
                h.append(k),
                g.append(h);
                for (var p = new Date(f.getTime()), q = new Date(this.options.startYear, d + 1, 0); p.getDay() != m;) p.setDate(p.getDate() - 1);
                for (; p <= q;) {
                        var r = a(document.createElement("tr"));
                        if (this.options.displayWeekNumber) {
                            var l = a(document.createElement("td"));
                            l.addClass("week-number"),
                            l.text(this.getWeekNumber(p)),
                            r.append(l)
                        }
                        do {
                            var s = a(document.createElement("td"));
                            if (s.addClass("day"), this._isHidden(p.getDay()) && s.addClass("hidden"), p < f) s.addClass("old");
                            else if (p > q) s.addClass("new");
                            else {
                                this._isDisabled(p) && s.addClass("disabled");
                                var t = a(document.createElement("div"));
                                t.addClass("day-content"),
                                t.text(p.getDate()),
                                s.append(t),
                                this.options.customDayRenderer && this.options.customDayRenderer(t, p)
                            }
                            r.append(s), p.setDate(p.getDate() + 1)
                        } while (p.getDay() != m);
                        g.append(r)
                    }
                e.append(g),
                b.append(e)
            }
            this.element.append(b)
        },
        _renderDataSource: function () {
            var b = this;
            null != this.options.dataSource && this.options.dataSource.length > 0 && this.element.find(".month-container").each(function () {
                var c = a(this).data("month-id"),
                    d = new Date(b.options.startYear, c, 1),
                    e = new Date(b.options.startYear, c + 1, 1);
                if ((null == b.options.minDate || e > b.options.minDate) && (null == b.options.maxDate || d <= b.options.maxDate)) {
                        for (var f = [], g = 0; g < b.options.dataSource.length; g++) b.options.dataSource[g].startDate >= e && !(b.options.dataSource[g].endDate < d) || f.push(b.options.dataSource[g]);
                        f.length > 0 && a(this).find(".day-content").each(function () {
                            var d = new Date(b.options.startYear, c, a(this).text()),
                                e = new Date(b.options.startYear, c, d.getDate() + 1),
                                g = [];
                            if ((null == b.options.minDate || d >= b.options.minDate) && (null == b.options.maxDate || d <= b.options.maxDate)) {
                                for (var h = 0; h < f.length; h++) f[h].startDate < e && f[h].endDate >= d && g.push(f[h]);
                                    g.length > 0 && (b.options.displayDisabledDataSource || !b._isDisabled(d)) && b._renderDataSourceDay(a(this), d, g)
                                }
                        })
                    }
            })
        },
        _renderDataSourceDay: function (a, b, c) {
            switch (this.options.style) {
            case "border":
                var d = 0;
                if (1 == c.length ? d = 4 : c.length <= 3 ? d = 2 : a.parent().css("box-shadow", "inset 0 -4px 0 0 black"), d > 0) {
                    for (var e = "", f = 0; f < c.length; f++)"" != e && (e += ","),
                    e += "inset 0 -" + (parseInt(f) + 1) * d + "px 0 0 " + c[f].color;
                    a.parent().css("box-shadow", e)
                }
                break;
            case "background":
                a.parent().css("background-color", c[c.length - 1].color);
                var g = b.getTime();
                if (c[c.length - 1].startDate.getTime() == g) if (a.parent().addClass("day-start"), c[c.length - 1].startHalfDay || this.options.alwaysHalfDay) {
                    a.parent().addClass("day-half");
                    for (var h = "transparent", f = c.length - 2; f >= 0; f--) if (c[f].startDate.getTime() != g || !c[f].startHalfDay && !this.options.alwaysHalfDay) {
                        h = c[f].color;
                        break
                    }
                    a.parent().css("background", "linear-gradient(-45deg, " + c[c.length - 1].color + ", " + c[c.length - 1].color + " 49%, " + h + " 51%, " + h + ")")
                } else this.options.roundRangeLimits && a.parent().addClass("round-left");
                else if (c[c.length - 1].endDate.getTime() == g) if (a.parent().addClass("day-end"), c[c.length - 1].endHalfDay || this.options.alwaysHalfDay) {
                    a.parent().addClass("day-half");
                    for (var h = "transparent", f = c.length - 2; f >= 0; f--) if (c[f].endDate.getTime() != g || !c[f].endHalfDay && !this.options.alwaysHalfDay) {
                        h = c[f].color;
                        break
                    }
                    a.parent().css("background", "linear-gradient(135deg, " + c[c.length - 1].color + ", " + c[c.length - 1].color + " 49%, " + h + " 51%, " + h + ")")
                } else this.options.roundRangeLimits && a.parent().addClass("round-right");
                break;
            case "custom":
                this.options.customDataSourceRenderer && this.options.customDataSourceRenderer.call(this, a, b, c)
            }
        },
        _applyEvents: function () {
            var b = this;
            this.element.find(".year-neighbor, .year-neighbor2").click(function () {
                a(this).hasClass("disabled") || b.setYear(parseInt(a(this).text()))
            }),
            this.element.find(".calendar-header .prev").click(function () {
                a(this).hasClass("disabled") || b.element.find(".months-container").animate({
                    "margin-left": "100%"
                }, 100, function () {
                    b.element.find(".months-container").css("visibility", "hidden"),
                    b.element.find(".months-container").css("margin-left", "0"),
                    setTimeout(function () {
                        b.setYear(b.options.startYear - 1)
                    }, 50)
                })
            }),
            this.element.find(".calendar-header .next").click(function () {
                a(this).hasClass("disabled") || b.element.find(".months-container").animate({
                    "margin-left": "-100%"
                }, 100, function () {
                    b.element.find(".months-container").css("visibility", "hidden"),
                    b.element.find(".months-container").css("margin-left", "0"),
                    setTimeout(function () {
                        b.setYear(b.options.startYear + 1)
                    }, 50)
                })
            });
            var c = this.element.find(".day:not(.old, .new, .disabled)");
            c.click(function (c) {
                c.stopPropagation();
                var d = b._getDate(a(this));
                b._triggerEvent("clickDay", {
                    element: a(this),
                    which: c.which,
                    date: d,
                    events: b.getEvents(d)
                })
            }),
            c.bind("contextmenu", function (c) {
                b.options.enableContextMenu && (c.preventDefault(), b.options.contextMenuItems.length > 0 && b._openContextMenu(a(this)));
                var d = b._getDate(a(this));
                b._triggerEvent("dayContextMenu", {
                    element: a(this),
                    date: d,
                    events: b.getEvents(d)
                })
            }),
            this.options.enableRangeSelection && (c.mousedown(function (c) {
                if (1 == c.which) {
                    var d = b._getDate(a(this));
                    (b.options.allowOverlap || 0 == b.getEvents(d).length) && (b._mouseDown = !0, b._rangeStart = b._rangeEnd = d, b._refreshRange())
                }
            }), c.mouseenter(function (c) {
                if (b._mouseDown) {
                    var d = b._getDate(a(this));
                    if (!b.options.allowOverlap) {
                        var e = new Date(b._rangeStart.getTime());
                        if (e < d) for (var f = new Date(e.getFullYear(), e.getMonth(), e.getDate() + 1); e < d && !(b.getEvents(f).length > 0);) e.setDate(e.getDate() + 1),
                        f.setDate(f.getDate() + 1);
                        else for (var f = new Date(e.getFullYear(), e.getMonth(), e.getDate() - 1); e > d && !(b.getEvents(f).length > 0);) e.setDate(e.getDate() - 1),
                        f.setDate(f.getDate() - 1);
                        d = e
                    }
                    var g = b._rangeEnd;
                    b._rangeEnd = d,
                    g.getTime() != b._rangeEnd.getTime() && b._refreshRange()
                }
            }), a(window).mouseup(function (a) {
                if (b._mouseDown) {
                    b._mouseDown = !1,
                    b._refreshRange();
                    var c = b._rangeStart < b._rangeEnd ? b._rangeStart : b._rangeEnd,
                        d = b._rangeEnd > b._rangeStart ? b._rangeEnd : b._rangeStart;
                    b._triggerEvent("selectRange", {
                            startDate: c,
                            endDate: d,
                            events: b.getEventsOnRange(c, new Date(d.getFullYear(), d.getMonth(), d.getDate() + 1))
                        })
                }
            })),
            c.mouseenter(function (c) {
                if (!b._mouseDown) {
                    var d = b._getDate(a(this));
                    b._triggerEvent("mouseOnDay", {
                        element: a(this),
                        date: d,
                        events: b.getEvents(d)
                    })
                }
            }),
            c.mouseleave(function (c) {
                var d = b._getDate(a(this));
                b._triggerEvent("mouseOutDay", {
                    element: a(this),
                    date: d,
                    events: b.getEvents(d)
                })
            }),
            setInterval(function () {
                var c = a(b.element).width(),
                    d = a(b.element).find(".month").first().width() + 10,
                    e = "month-container";
                e += 6 * d < c ? " col-xs-2" : 4 * d < c ? " col-xs-3" : 3 * d < c ? " col-xs-4" : 2 * d < c ? " col-xs-6" : " col-xs-12",
                a(b.element).find(".month-container").attr("class", e)
            }, 20)
        },
        _refreshRange: function () {
            var b = this;
            if (this.element.find("td.day.range").removeClass("range"), this.element.find("td.day.range-start").removeClass("range-start"), this.element.find("td.day.range-end").removeClass("range-end"), this._mouseDown) {
                var e = b._rangeStart < b._rangeEnd ? b._rangeStart : b._rangeEnd,
                    f = b._rangeEnd > b._rangeStart ? b._rangeEnd : b._rangeStart;
                this.element.find(".month-container").each(function () {
                        var c = a(this).data("month-id");
                        e.getMonth() <= c && f.getMonth() >= c && a(this).find("td.day:not(.old, .new)").each(function () {
                            var c = b._getDate(a(this));
                            c >= e && c <= f && (a(this).addClass("range"), c.getTime() == e.getTime() && a(this).addClass("range-start"), c.getTime() == f.getTime() && a(this).addClass("range-end"))
                        })
                    })
            }
        },
        _openContextMenu: function (b) {
            var c = a(".calendar-context-menu");
            c.length > 0 ? (c.hide(), c.empty()) : (c = a(document.createElement("div")), c.addClass("calendar-context-menu"), a("body").append(c));
            for (var d = this._getDate(b), e = this.getEvents(d), f = 0; f < e.length; f++) {
                var g = a(document.createElement("div"));
                g.addClass("item"),
                g.css("border-left", "4px solid " + e[f].color);
                var h = a(document.createElement("div"));
                h.addClass("content"),
                h.text(e[f].name),
                g.append(h);
                var i = a(document.createElement("span"));
                i.addClass("glyphicon glyphicon-chevron-right"),
                g.append(i),
                this._renderContextMenuItems(g, this.options.contextMenuItems, e[f]),
                c.append(g)
            }
            c.children().length > 0 && (c.css("left", b.offset().left + 25 + "px"), c.css("top", b.offset().top + 25 + "px"), c.show(), a(window).one("mouseup", function () {
                c.hide()
            }))
        },
        _renderContextMenuItems: function (b, c, d) {
            var e = a(document.createElement("div"));
            e.addClass("submenu");
            for (var f = 0; f < c.length; f++) if (!c[f].visible || c[f].visible(d)) {
                var g = a(document.createElement("div"));
                g.addClass("item");
                var h = a(document.createElement("div"));
                h.addClass("content"),
                h.text(c[f].text),
                g.append(h),
                c[f].click && !
                function (a) {
                    g.click(function () {
                        c[a].click(d)
                    })
                }(f);
                var i = a(document.createElement("span"));
                i.addClass("glyphicon glyphicon-chevron-right"),
                g.append(i),
                c[f].items && c[f].items.length > 0 && this._renderContextMenuItems(g, c[f].items, d),
                e.append(g)
            }
            e.children().length > 0 && b.append(e)
        },
        _getColor: function (b) {
            var c = a("<div />");
            c.css("color", b)
        },
        _getDate: function (a) {
            var b = a.children(".day-content").text(),
                c = a.closest(".month-container").data("month-id"),
                d = this.options.startYear;
            return new Date(d, c, b)
        },
        _triggerEvent: function (b, c) {
            var d = a.Event(b);
            for (var e in c) d[e] = c[e];
            return this.element.trigger(d),
            d
        },
        _isDisabled: function (a) {
            if (null != this.options.minDate && a < this.options.minDate || null != this.options.maxDate && a > this.options.maxDate) return !0;
            if (this.options.disabledWeekDays.length > 0) for (var b = 0; b < this.options.disabledWeekDays.length; b++) if (a.getDay() == this.options.disabledWeekDays[b]) return !0;
            if (this.options.disabledDays.length > 0) for (var b = 0; b < this.options.disabledDays.length; b++) if (a.getTime() == this.options.disabledDays[b].getTime()) return !0;
            return !1
        },
        _isHidden: function (a) {
            if (this.options.hiddenWeekDays.length > 0) for (var b = 0; b < this.options.hiddenWeekDays.length; b++) if (a == this.options.hiddenWeekDays[b]) return !0;
            return !1
        },
        getWeekNumber: function (a) {
            var b = new Date(a.getTime());
            b.setHours(0, 0, 0, 0),
            b.setDate(b.getDate() + 3 - (b.getDay() + 6) % 7);
            var c = new Date(b.getFullYear(), 0, 4);
            return 1 + Math.round(((b.getTime() - c.getTime()) / 864e5 - 3 + (c.getDay() + 6) % 7) / 7)
        },
        getEvents: function (a) {
            return this.getEventsOnRange(a, new Date(a.getFullYear(), a.getMonth(), a.getDate() + 1))
        },
        getEventsOnRange: function (a, b) {
            var c = [];
            if (this.options.dataSource && a && b) for (var d = 0; d < this.options.dataSource.length; d++) this.options.dataSource[d].startDate < b && this.options.dataSource[d].endDate >= a && c.push(this.options.dataSource[d]);
            return c
        },
        getYear: function () {
            return this.options.startYear
        },
        setYear: function (a) {
            var b = parseInt(a);
            if (!isNaN(b)) {
                this.options.startYear = b,
                this.element.empty(),
                this.options.displayHeader && this._renderHeader();
                var c = this._triggerEvent("yearChanged", {
                    currentYear: this.options.startYear,
                    preventRendering: !1
                });
                c.preventRendering || this.render()
            }
        },
        getMinDate: function () {
            return this.options.minDate
        },
        setMinDate: function (a, b) {
            a instanceof Date && (this.options.minDate = a, b || this.render())
        },
        getMaxDate: function () {
            return this.options.maxDate
        },
        setMaxDate: function (a, b) {
            a instanceof Date && (this.options.maxDate = a, b || this.render())
        },
        getStyle: function () {
            return this.options.style
        },
        setStyle: function (a, b) {
            this.options.style = "background" == a || "border" == a || "custom" == a ? a : "border",
            b || this.render()
        },
        getAllowOverlap: function () {
            return this.options.allowOverlap
        },
        setAllowOverlap: function (a) {
            this.options.allowOverlap = a
        },
        getDisplayWeekNumber: function () {
            return this.options.displayWeekNumber
        },
        setDisplayWeekNumber: function (a, b) {
            this.options.displayWeekNumber = a,
            b || this.render()
        },
        getDisplayHeader: function () {
            return this.options.displayHeader
        },
        setDisplayHeader: function (a, b) {
            this.options.displayHeader = a,
            b || this.render()
        },
        getDisplayDisabledDataSource: function () {
            return this.options.displayDisabledDataSource
        },
        setDisplayDisabledDataSource: function (a, b) {
            this.options.displayDisabledDataSource = a,
            b || this.render()
        },
        getAlwaysHalfDay: function () {
            return this.options.alwaysHalfDay
        },
        setAlwaysHalfDay: function (a, b) {
            this.options.alwaysHalfDay = a,
            b || this.render()
        },
        getEnableRangeSelection: function () {
            return this.options.enableRangeSelection
        },
        setEnableRangeSelection: function (a, b) {
            this.options.enableRangeSelection = a,
            b || this.render()
        },
        getDisabledDays: function () {
            return this.options.disabledDays
        },
        setDisabledDays: function (a, b) {
            this.options.disabledDays = a instanceof Array ? a : [],
            b || this.render()
        },
        getDisabledWeekDays: function () {
            return this.options.disabledWeekDays
        },
        setDisabledWeekDays: function (a, b) {
            this.options.disabledWeekDays = a instanceof Array ? a : [],
            b || this.render()
        },
        getHiddenWeekDays: function () {
            return this.options.hiddenWeekDays
        },
        setHiddenWeekDays: function (a, b) {
            this.options.hiddenWeekDays = a instanceof Array ? a : [],
            b || this.render()
        },
        getRoundRangeLimits: function () {
            return this.options.roundRangeLimits
        },
        setRoundRangeLimits: function (a, b) {
            this.options.roundRangeLimits = a,
            b || this.render()
        },
        getEnableContextMenu: function () {
            return this.options.enableContextMenu
        },
        setEnableContextMenu: function (a, b) {
            this.options.enableContextMenu = a,
            b || this.render()
        },
        getContextMenuItems: function () {
            return this.options.contextMenuItems
        },
        setContextMenuItems: function (a, b) {
            this.options.contextMenuItems = a instanceof Array ? a : [],
            b || this.render()
        },
        getCustomDayRenderer: function () {
            return this.options.customDayRenderer
        },
        setCustomDayRenderer: function (b, c) {
            this.options.customDayRenderer = a.isFunction(b) ? b : null,
            c || this.render()
        },
        getCustomDataSourceRenderer: function () {
            return this.options.customDataSourceRenderer
        },
        setCustomDataSourceRenderer: function (b, c) {
            this.options.customDataSourceRenderer = a.isFunction(b) ? b : null,
            c || this.render()
        },
        getLanguage: function () {
            return this.options.language
        },
        setLanguage: function (a, b) {
            null != a && null != c[a] && (this.options.language = a, b || this.render())
        },
        getDataSource: function () {
            return this.options.dataSource
        },
        setDataSource: function (a, b) {
            this.options.dataSource = a instanceof Array ? a : [],
            this._initializeDatasourceColors(),
            b || this.render()
        },
        getWeekStart: function () {
            return this.options.weekStart ? this.options.weekStart : c[this.options.language].weekStart
        },
        setWeekStart: function (a, b) {
            this.options.weekStart = isNaN(parseInt(a)) ? null : parseInt(a),
            b || this.render()
        },
        addEvent: function (a, b) {
            this.options.dataSource.push(a),
            b || this.render()
        }
    },
    a.fn.calendar = function (c) {
        var d = new b(a(this), c);
        return a(this).data("calendar", d),
        d
    },
    a.fn.yearChanged = function (b) {
        a(this).bind("yearChanged", b)
    },
    a.fn.renderEnd = function (b) {
        a(this).bind("renderEnd", b)
    },
    a.fn.clickDay = function (b) {
        a(this).bind("clickDay", b)
    },
    a.fn.dayContextMenu = function (b) {
        a(this).bind("dayContextMenu", b)
    },
    a.fn.selectRange = function (b) {
        a(this).bind("selectRange", b)
    },
    a.fn.mouseOnDay = function (b) {
        a(this).bind("mouseOnDay", b)
    },
    a.fn.mouseOutDay = function (b) {
        a(this).bind("mouseOutDay", b)
    };
    var c = a.fn.calendar.dates = {
        en: {
            days: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
            daysShort: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
            daysMin: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"],
            months: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
            monthsShort: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
            weekShort: "W",
            weekStart: 0
        }
    },
        d = a.fn.calendar.colors = ["#2C8FC9", "#9CB703", "#F5BB00", "#FF4A32", "#B56CE2", "#45A597"];
    a(function () {
            a('[data-provide="calendar"]').each(function () {
                a(this).calendar()
            })
        })
}(window.jQuery);