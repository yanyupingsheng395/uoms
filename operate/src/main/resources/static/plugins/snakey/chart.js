// https://observablehq.com/d/17dade61f9256371@136
export default function define(runtime, observer) {
    const main = runtime.module();
    main.variable( observer( "svg" ) ).define( "svg", ["d3", "DOM", "width", "height", "graph", "margin", "format", "sankey", "arrow", "duration"], function (d3, DOM, width, height, graph, margin, format, sankey, arrow, duration) {
            const svg = d3.select( DOM.svg( width, height ) );

            const defs = svg.append( "defs" );

            // Add definitions for all of the linear gradients.
            const gradients = defs.selectAll( "linearGradient" )
                .data( graph.links )
                .join( "linearGradient" )
                .attr( "id", d => d.gradient.id )
            gradients.append( "stop" ).attr( "offset", 0.0 ).attr( "stop-color", d => d.source.color );
            gradients.append( "stop" ).attr( "offset", 1.0 ).attr( "stop-color", d => d.target.color );

            // Add a g.view for holding the sankey diagram.
            const view = svg.append( "g" )
                .classed( "view", true )
                .attr( "transform", `translate(${margin}, ${margin})` );

            // Define the nodes.
            const nodes = view.selectAll( "rect.node" )
                .data( graph.nodes )
                .join( "rect" )
                .classed( "node", true )
                .attr( "id", d => `node-${d.index}` )
                .attr( "x", d => d.x0 )
                .attr( "y", d => d.y0 )
                .attr( "width", d => d.x1 - d.x0 )
                .attr( "height", d => Math.max( 1, d.y1 - d.y0 ) )
                .attr( "fill", d => d.color )
                .attr( "opacity", 0.9 );

            // Add titles for node hover effects.
            nodes.append( "title" ).text( d => `${d.name}\n${format( d.value )}` );

            // Add text labels.
            view.selectAll( "text.node" )
                .data( graph.nodes )
                .join( "text" )
                .classed( "node", true )
                .attr( "x", d => d.x1 )
                .attr( "dx", 6 )
                .attr( "y", d => (d.y1 + d.y0) / 2 )
                .attr( "dy", "0.35em" )
                .attr( "fill", "black" )
                .attr( "text-anchor", "start" )
                .attr( "font-size", 14 )
                .attr( "font-family", "Arial, sans-serif" )
                .text( d => d.name )
                .filter( d => d.x1 > width / 2 )
                .attr( "x", d => d.x0 )
                .attr( "dx", -6 )
                .attr( "text-anchor", "end" );

            // Define the gray links.
            const links = view.selectAll( "path.link" )
                .data( graph.links )
                .join( "path" )
                .classed( "link", true )
                .attr( "d", sankey.sankeyLinkHorizontal() )
                .attr( "stroke", "#33cabb" )
                .attr( "stroke-opacity", 0.1 )
                .attr( "stroke-width", d => Math.max( 1, d.width ) )
                .attr( "fill", "none" );

            // Add <title> hover effect on links.
            links.append( "title" ).text( d => `${d.source.name} ${arrow} ${d.target.name}\n${format( d.value )}` );

            // Define the default dash behavior for colored gradients.
            function setDash(link) {
                let el = view.select( `#${link.path.id}` );
                let length = el.node().getTotalLength();
                el.attr( "stroke-dasharray", `${length} ${length}` )
                    .attr( "stroke-dashoffset", length );
            }

            const gradientLinks = view.selectAll( "path.gradient-link" )
                .data( graph.links )
                .join( "path" )
                .classed( "gradient-link", true )
                .attr( "id", d => d.path.id )
                .attr( "d", sankey.sankeyLinkHorizontal() )
                .attr( "stroke", d => d.gradient )
                .attr( "stroke-opacity", 0 )
                .attr( "stroke-width", d => Math.max( 1, d.width ) )
                .attr( "fill", "none" )
                .each( setDash );

            function branchAnimate(node, res) {
                let links = view.selectAll( "path.gradient-link" )
                    .filter( (link) => {
                        return node.sourceLinks.indexOf( link ) !== -1;
                    } );
                let nextNodes = [];
                links.each( (link) => {
                    nextNodes.push( link.target );
                });
                res.push(links);
                nextNodes.forEach( (node) => {
                    branchAnimate(node, res);
                });
                // links.attr( "stroke-opacity", 0.5 )
                //     .transition()
                //     .duration( duration )
                //     .ease( d3.easeLinear )
                //     .attr( "stroke-dashoffset", 0 )
                //     .on( "end", () => {
                //         nextNodes.forEach( (node) => {
                //             branchAnimate( node );
                //         } );
                //     } );

                // 需要返回的数据
                //
            }
        function branchAnimate2(node)
        {
            //清空之前的操作
            gradientLinks.transition();
            gradientLinks.attr("stroke-opactiy", 0)
                .each(setDash);

            $.get("/sankey/getSunIdList", {id: 26}, function (r) {
                var ids=[];
                $.each(r, function (k, v) {
                    ids.push(parseInt(v));
                });
                // ids.push()
                let links = svg.selectAll("path.gradient-link")
                    .filter((link) => {
                        return ids.indexOf(link.id) !== -1;
                    });
                console.log(typeof links);
                console.log(links);
                links.attr("stroke-opacity", 0.5)
                    .transition()
                    .duration(duration)
                    .ease(d3.easeLinear)
                    .attr("stroke-dashoffset", 0);
            });

        }


            // function test(node) {
            //     var res = [];
            //     branchAnimate(node, res);
            //     console.log(res);
            //     res.forEach((link) => {
            //         link.attr( "stroke-opacity", 0.5 )
            //             .transition()
            //             .duration( duration )
            //             .ease( d3.easeLinear )
            //             .attr( "stroke-dashoffset", 0 );
            //     });
            // }

            function branchClear() {
                gradientLinks.transition();
                gradientLinks.attr( "stroke-opactiy", 0 )
                    .each( setDash );
            }

            // nodes.on( "mouseover", test)
            //     .on( "mouseout", branchClear);

            nodes.on( "click", branchAnimate2);

            return svg.node();
        }
    );
    main.variable().define( "graph", ["layout", "energy", "color", "DOM"], function (layout, energy, color, DOM) {
            const graph = layout( energy );

            graph.nodes.forEach( (node) => {
                node.color = color( node.index / graph.nodes.length );
            } );

            graph.links.forEach( (link) => {
                link.gradient = DOM.uid("gradient");
                link.path = DOM.uid("path");
            });
            return graph;
        }
    );
    main.variable().define( "layout", ["sankey", "size", "nodePadding", "nodeWidth"], function (sankey, size, nodePadding, nodeWidth) {
        return (
            sankey.sankey()
                .size( size )
                .nodePadding( nodePadding )
                .nodeWidth( nodeWidth )
        )
    } );
    main.variable().define( "format", ["d3"], function (d3) {
        return (
            (value) => {
                let f = d3.format( ",.0f" );
                return "购买人次：" + f( value );
            }
        )
    } );
    main.variable().define( "color", ["d3"], function (d3) {
        return (
            (value) => {
                return d3.interpolateRainbow( value );
            }
        )
    } );
    main.variable().define( "energy", function () {
        $MB.loadingDesc("show", "正在加载数据...");
        return (
            $.get( "/sankey/getSpuSnakey", {}, function (r) {
                $MB.loadingDesc("hide");
                $("#chart").show();
            })
        )
    } );
    // main.variable().define("energy", function(){
    //     $("#chart").show();
    //         return(
    //             {"nodes":[
    //                     {"name":"Agricultural 'waste'"},
    //                     {"name":"Bio-conversion"},
    //                     {"name":"Liquid"},
    //                     {"name":"Losses"},
    //                     {"name":"Solid"},
    //                     {"name":"Gas"},
    //                     {"name":"Biofuel imports"},
    //                     {"name":"Biomass imports"},
    //                     {"name":"Coal imports"},
    //                     {"name":"Coal"},
    //                     {"name":"Coal reserves"},
    //                     {"name":"District heating"},
    //                     {"name":"Industry"},
    //                     {"name":"Heating and cooling - commercial"},
    //                     {"name":"Heating and cooling - homes"},
    //                     {"name":"Electricity grid"},
    //                     {"name":"Over generation / exports"},
    //                     {"name":"H2 conversion"},
    //                     {"name":"Road transport"},
    //                     {"name":"Agriculture"},
    //                     {"name":"Rail transport"},
    //                     {"name":"Lighting & appliances - commercial"},
    //                     {"name":"Lighting & appliances - homes"},
    //                     {"name":"Gas imports"},
    //                     {"name":"Ngas"},
    //                     {"name":"Gas reserves"},
    //                     {"name":"Thermal generation"},
    //                     {"name":"Geothermal"},
    //                     {"name":"H2"},
    //                     {"name":"Hydro"},
    //                     {"name":"International shipping"},
    //                     {"name":"Domestic aviation"},
    //                     {"name":"International aviation"},
    //                     {"name":"National navigation"},
    //                     {"name":"Marine algae"},
    //                     {"name":"Nuclear"},
    //                     {"name":"Oil imports"},
    //                     {"name":"Oil"},
    //                     {"name":"Oil reserves"},
    //                     {"name":"Other waste"},
    //                     {"name":"Pumped heat"},
    //                     {"name":"Solar PV"},
    //                     {"name":"Solar Thermal"},
    //                     {"name":"Solar"},
    //                     {"name":"Tidal"},
    //                     {"name":"UK land based bioenergy"},
    //                     {"name":"Wave"},
    //                     {"name":"Wind"}
    //                 ],
    //                 "links":[
    //                     {"source":0,"target":1,"value":124.729,id:1},
    //                     {"source":1,"target":2,"value":0.597,id:2},
    //                     {"source":1,"target":3,"value":26.862,id:3},
    //                     {"source":1,"target":4,"value":280.322,id:4},
    //                     {"source":1,"target":5,"value":81.144,id:5},
    //                     {"source":6,"target":2,"value":35,id:6},
    //                     {"source":7,"target":4,"value":35,id:7},
    //                     {"source":8,"target":9,"value":11.606,id:8},
    //                     {"source":10,"target":9,"value":63.965,id:9},
    //                     {"source":9,"target":4,"value":75.571,id:10},
    //                     {"source":11,"target":12,"value":10.639,id:11},
    //                     {"source":11,"target":13,"value":22.505,id:12},
    //                     {"source":11,"target":14,"value":46.184,id:13},
    //                     {"source":15,"target":16,"value":104.453,id:14},
    //                     {"source":15,"target":14,"value":113.726,id:15},
    //                     {"source":15,"target":17,"value":27.14,id:16},
    //                     {"source":15,"target":12,"value":342.165,id:17},
    //                     {"source":15,"target":18,"value":37.797,id:18},
    //                     {"source":15,"target":19,"value":4.412,id:19},
    //                     {"source":15,"target":13,"value":40.858,id:20},
    //                     {"source":15,"target":3,"value":56.691,id:21},
    //                     {"source":15,"target":20,"value":7.863},
    //                     {"source":15,"target":21,"value":90.008},
    //                     {"source":15,"target":22,"value":93.494},
    //                     {"source":23,"target":24,"value":40.719},
    //                     {"source":25,"target":24,"value":82.233},
    //                     {"source":5,"target":13,"value":0.129},
    //                     {"source":5,"target":3,"value":1.401},
    //                     {"source":5,"target":26,"value":151.891},
    //                     {"source":5,"target":19,"value":2.096},
    //                     {"source":5,"target":12,"value":48.58},
    //                     {"source":27,"target":15,"value":7.013},
    //                     {"source":17,"target":28,"value":20.897},
    //                     {"source":17,"target":3,"value":6.242},
    //                     {"source":28,"target":18,"value":20.897},
    //                     {"source":29,"target":15,"value":6.995},
    //                     {"source":2,"target":12,"value":121.066},
    //                     {"source":2,"target":30,"value":128.69},
    //                     {"source":2,"target":18,"value":135.835},
    //                     {"source":2,"target":31,"value":14.458},
    //                     {"source":2,"target":32,"value":206.267},
    //                     {"source":2,"target":19,"value":3.64},
    //                     {"source":2,"target":33,"value":33.218},
    //                     {"source":2,"target":20,"value":4.413},
    //                     {"source":34,"target":1,"value":4.375},
    //                     {"source":24,"target":5,"value":122.952},
    //                     {"source":35,"target":26,"value":839.978},
    //                     {"source":36,"target":37,"value":504.287},
    //                     {"source":38,"target":37,"value":107.703},
    //                     {"source":37,"target":2,"value":611.99},
    //                     {"source":39,"target":4,"value":56.587},
    //                     {"source":39,"target":1,"value":77.81},
    //                     {"source":40,"target":14,"value":193.026},
    //                     {"source":40,"target":13,"value":70.672},
    //                     {"source":41,"target":15,"value":59.901},
    //                     {"source":42,"target":14,"value":19.263},
    //                     {"source":43,"target":42,"value":19.263},
    //                     {"source":43,"target":41,"value":59.901},
    //                     {"source":4,"target":19,"value":0.882},
    //                     {"source":4,"target":26,"value":400.12},
    //                     {"source":4,"target":12,"value":46.477},
    //                     {"source":26,"target":15,"value":525.531},
    //                     {"source":26,"target":3,"value":787.129},
    //                     {"source":26,"target":11,"value":79.329},
    //                     {"source":44,"target":15,"value":9.452},
    //                     {"source":45,"target":1,"value":182.01},
    //                     {"source":46,"target":15,"value":19.013},
    //                     {"source":47,"target":15,"value":289.366}
    //                 ]}
    //         )}
    //     );

    main.variable().define( "size", ["width", "margin", "height"], function (width, margin, height) {
        return (
            [width - 2 * margin, height - 2 * margin]
        )
    } );
    main.variable().define( "height", function () {
        return (
            800
        )
    } );
    // main.variable().define( "width", function () {
    //     return (
    //         (document.getElementById( "chart" ).clientWidth || document.getElementById( "chart" ).offsetWidth) - 43
    //     )
    // } );

    main.variable().define( "width", function () {
        return (
            2400
        )
    } );

    main.variable().define( "margin", function () {
        return (
            20
        )
    } );
    main.variable().define( "nodeWidth", function () {
        return (
            20
        )
    } );
    main.variable().define( "nodePadding", function () {
        return (
            20
        )
    } );
    main.variable().define( "duration", function () {
        return (
            250
        )
    } );
    main.variable().define( "arrow", function () {
        return (
            "\u2192"
        )
    } );
    main.variable().define( "d3", ["require"], function (require) {
        return (
            require( "d3@5.9" )
        )
    } );
    main.variable().define( "sankey", ["require"], function (require) {
        return (
            require( "d3-sankey@0.12" )
        )
    } );
    return main;
}

