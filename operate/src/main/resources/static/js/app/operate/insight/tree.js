class ztree {
    constructor(opt) {
        const ops = {
            tree_id: "",
            spu_product_type: "",
            spu_product_id: "",
            spu_product_name: "",
            tree_content: ""
        };
        this.ops = Object.assign({}, ops, opt);
    }

    // 指标随购买次数变化曲线初始化条件
    initZtree() {
        var ops = this.ops;
        var left = $(this.ops.spu_product_name).offset().left - $(".row").offset().left - 14;
        var width = $(this.ops.spu_product_name).width() + 5;
        $(this.ops.tree_id).attr("style", "margin-left:"+ left +"px;width:"+width+"px;");
        const promise = new Promise(function (resolve, reject) {
            $.get("/insight/getSpuTree", {}, function (r){
                if(r.code === 200) {
                    resolve(r);
                } else {
                    reject(r);
                }
            });
        });

        var setting = {
            async: {
                enable: true,
                url: getUrl
            },
            check: {
                enable: true,
                chkStyle: "radio",
                radioType: "all"
            },
            view: {
                dblClickExpand: false,
                showIcon: false
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                onClick: onClick,
                onCheck: onCheck,
                beforeExpand: beforeExpand,
                onAsyncSuccess: onAsyncSuccess,
                onAsyncError: onAsyncError
            }
        };

        async function makeTree() {
            let result = await promise;
            $.fn.zTree.init($(ops.tree_id), setting, result.data);
        }
        makeTree();

        function onClick(e, treeId, treeNode) {
            var treeId = ops.tree_id.split("#")[1];
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            zTree.checkNode(treeNode, !treeNode.checked, null, true);
            return false;
        }

        function onCheck(e, treeId, treeNode) {
            var treeId = ops.tree_id.split("#")[1];
            var zTree = $.fn.zTree.getZTreeObj(treeId),
                nodes = zTree.getCheckedNodes(true),
                v = "";
            for (var i=0, l=nodes.length; i<l; i++) {
                v += nodes[i].name + ",";
            }
            if (v.length > 0 ) v = v.substring(0, v.length-1);
            $(ops.spu_product_name).val(v);
            $(ops.spu_product_id).val(nodes[0].id);
            $(ops.tree_content).toggle();
            if(treeNode.isParent) {
                $(ops.spu_product_type).val("spu");
            }else {
                $(ops.spu_product_type).val("product");
            }
        }

        function beforeExpand(treeId, treeNode) {
            if (!treeNode.isAjaxing) {
                ajaxGetNodes(treeNode, "refresh");
                return true;
            } else {
                $MB.n_warning("正在下载数据中，请稍后展开节点...");
                return false;
            }
        }
        var className = "dark";
        function onAsyncSuccess(event, treeId, treeNode, msg) {
            if (!msg || msg.length == 0) {
                return;
            }
            var treeId = ops.tree_id.split("#")[1];
            var zTree = $.fn.zTree.getZTreeObj(treeId),
                totalCount = treeNode.count;
            if (treeNode.children.length < totalCount) {
                setTimeout(function() {ajaxGetNodes(treeNode);}, perTime);
            } else {
                treeNode.icon = "";
                zTree.updateNode(treeNode);
                zTree.selectNode(treeNode.children[0]);
                className = (className === "dark" ? "":"dark");
            }
        }
        function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
            var treeId = ops.tree_id.split("#")[1];
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            $MB.n_danger("异步获取数据出现异常。");
            treeNode.icon = "";
            zTree.updateNode(treeNode);
        }
        function ajaxGetNodes(treeNode, reloadType) {
            var treeId = ops.tree_id.split("#")[1];
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            if (reloadType == "refresh") {
                zTree.updateNode(treeNode);
            }
            zTree.reAsyncChildNodes(treeNode, reloadType, true);
        }

        function getUrl(treeId, treeNode) {
            return "/insight/getProductTree?spuWid=" + treeNode.id;
        }
    }
}

new ztree({
    tree_id: "#ztree1",
    spu_product_type: "#spuProductType1",
    spu_product_id: "#spuProductId1",
    spu_product_name: "#spuProductName1",
    tree_content: "#ztreeContent1"
}).initZtree();

new ztree({
    tree_id: "#ztree2",
    spu_product_type: "#spuProductType2",
    spu_product_id: "#spuProductId2",
    spu_product_name: "#spuProductName2",
    tree_content: "#ztreeContent2"
}).initZtree();