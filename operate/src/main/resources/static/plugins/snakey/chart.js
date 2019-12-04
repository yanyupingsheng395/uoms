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
            nodes.append( "title" ).text( d => `${d.name}\n${format( d.value)}` );

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
                .attr( "font-size", 12)
                .attr( "font-family", "Arial, sans-serif")
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
                .attr( "stroke", "#87CEFA" )
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
                    .attr( "stroke-dashoffset", length);
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

            // 显示全局
            function branchAnimate_click(node) {
                var links = node.sourceLinks;
                var nodeArray = [];
                nodeArray.push( node );
                while (links.length !== 0) {
                    links.forEach( x => {
                        var tempNode = x.target;
                        nodeArray.push( tempNode );
                        links = tempNode.sourceLinks;
                    } );
                }
                nodeArray.forEach( (n) => {
                    let links = view.selectAll( "path.gradient-link" )
                        .filter( (link) => {
                            return n.sourceLinks.indexOf( link ) !== -1;
                        });
                    // links.attr( "stroke-opacity", 0.5).transition().duration( duration ).ease( d3.easeLinear ).attr( "stroke-dashoffset", 0 );
                    links.attr( "stroke-opacity", 0.3 ).attr( "stroke-dashoffset", 0 );
                } );
            }

            function branchAnimate_over(node) {
                let links = view.selectAll( "path.gradient-link" )
                    .filter( (link) => {
                        return node.sourceLinks.indexOf( link ) !== -1;
                    } );
                console.log(links);
                let nextNodes = [];
                links.each( (link) => {
                    nextNodes.push( link.target );
                } );
                links.attr( "stroke-opacity", 0.3 )
                    .attr( "stroke-dashoffset", 0 );
            }

            function branchClear() {
                gradientLinks.transition();
                gradientLinks.attr( "stroke-opactiy", 0 )
                    .each( setDash );
            }

            nodes.on( "mouseover", branchAnimate_over).on( "mouseout", branchClear );
            nodes.on('click', branchAnimate_click).on( "mouseout", branchClear );
            return svg.node();
        }
    );
    main.variable().define( "graph", ["layout", "energy", "color", "DOM"], function (layout, energy, color, DOM) {
            const graph = layout( energy );
            graph.nodes.forEach( (node) => {
                node.color = color( node.index / graph.nodes.length );
            } );
            graph.links.forEach( (link) => {
                link.gradient = DOM.uid( "gradient" );
                link.path = DOM.uid( "path" );
            } );
            return graph;
        }
    );
    main.variable().define( "layout", ["sankey", "size", "nodePadding", "nodeWidth"], function (sankey, size, nodePadding, nodeWidth) {
        return (
            sankey.sankey()
                .size( size )
                .nodePadding( nodePadding )
                .nodeWidth(nodeWidth)
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
        $MB.loadingDesc( "show", "正在加载数据..." );
        return (
            $.get( "/sankey/getSpuSnakey", {}, function (r) {
                $MB.loadingDesc( "hide" );
                $( "#chart" ).show();
            } )
        )
    } );

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

    main.variable().define( "width", function () {
        return (
            1400
        )
    } );

    main.variable().define( "margin", function () {
        return (
            10
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
            220
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

