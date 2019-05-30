var diagId = $("#diagId").val();
var jm;
$.post("/diag/getNodes", {diagId: diagId}, function (r) {
    jmInit(r.data);
});

function jmInit(data) {
    var mind = {
        "meta":{
            "name":"gmv_mind",
            "version":"0.2"
        },
        "format":"node_array",
        "data": data
    };
    var options = {
        container:'jsmind_container',
        editable:false,
        theme:'greensea',
        mode: "side"
    };
    jm = jsMind.show(options,mind);
    addEventListenerOfNode();
}