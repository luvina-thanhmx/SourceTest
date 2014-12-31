/*
  @license
  (c) 2009-2010 Michael Leibman (michael.leibman@gmail.com)
  All rights reserved.
*/

(function() {
    function GridNicGridPager(dataView, grid, $container, columns)
    {
        var $status, $contextMenu;

        function init()
        {
            dataView.onPagingInfoChanged.subscribe(function(pagingInfo) {
                updatePager(pagingInfo);
            });

            constructPagerUI();
            updatePager(dataView.getPagingInfo());
        }

		function getNavState()
		{
			var cannotLeaveEditMode = !GridNic.GlobalEditorLock.commitCurrentEdit();
			var pagingInfo = dataView.getPagingInfo();
			var lastPage = Math.floor(pagingInfo.totalRows/pagingInfo.pageSize);

            return {
                canGotoFirst:	!cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum > 0,
                canGotoLast:	!cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum != lastPage,
                canGotoPrev:	!cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum > 0,
                canGotoNext:	!cannotLeaveEditMode && pagingInfo.pageSize != 0 && pagingInfo.pageNum < lastPage,
                pagingInfo:		pagingInfo,
                lastPage:		lastPage
            }
        }

        function setPageSize(n)
        {
            dataView.setPagingOptions({pageSize:n});
        }

        function gotoFirst()
        {
            if (getNavState().canGotoFirst)
                dataView.setPagingOptions({pageNum: 0});
        }

        function gotoLast()
        {
            var state = getNavState();
            if (state.canGotoLast)
                dataView.setPagingOptions({pageNum: state.lastPage});
        }

        function gotoPrev()
        {
            var state = getNavState();
            if (state.canGotoPrev)
                dataView.setPagingOptions({pageNum: state.pagingInfo.pageNum-1});
        }

        function gotoNext()
        {
            var state = getNavState();
            if (state.canGotoNext)
                dataView.setPagingOptions({pageNum: state.pagingInfo.pageNum+1});
        }

        function constructPagerUI()
        {
            $container.empty();

            $status = $("<span class='gridnic-pager-status' />").appendTo($container);

            var $nav = $("<span class='gridnic-pager-nav' />").appendTo($container);
            var $settings = $("<span class='gridnic-pager-settings' />").appendTo($container);

            $settings.append("<span class='gridnic-pager-settings-expanded' style='display:inline'>Show: <a data=0>All</a><a data='-1'>Auto</a><a data=25>25</a><a data=50>50</a><a data=100>100</a></span>");

            $settings.find("a[data]").click(function(e) {
                var pagesize = $(e.target).attr("data");
                if (pagesize != undefined)
                {
                    if (pagesize == -1)
                    {
                        var vp = grid.getViewport();
                        setPageSize(vp.bottom-vp.top);
                    }
                    else
                        setPageSize(parseInt(pagesize));
                }
            });

            $("<span class='ui-icon ui-icon-lightbulb' />")
                    .click(function() { $(".gridnic-pager-settings-expanded").toggle() })
                    .appendTo($settings);

            $("<span class='ui-icon ui-icon-seek-first' />")
                    .click(gotoFirst)
                    .appendTo($nav);

            $("<span class='ui-icon ui-icon-seek-prev' />")
                    .click(gotoPrev)
                    .appendTo($nav);

            $("<span class='ui-icon ui-icon-seek-next' />")
                    .click(gotoNext)
                    .appendTo($nav);

            $("<span class='ui-icon ui-icon-seek-end' />")
                    .click(gotoLast)
                    .appendTo($nav);

            $container.find(".ui-icon")
                    .addClass("ui-state-default ui-corner-all")
                    .mouseover(function(e) {
                        $(e.target).addClass("ui-state-hover")
                    })
                    .mouseout(function(e) {
                        $(e.target).removeClass("ui-state-hover")
                    });
					
            $outputs = $("<span class='gridnic-pager-output' />").appendTo($container);
			$("<span class='ui-icon ui-output-pdf' title='Pdf' />").click(outputPdf).appendTo($outputs);			

            $outputs = $("<span class='gridnic-pager-output' />").appendTo($container);
			$("<span class='ui-icon ui-output-word' title='Word' />").click(outputWord).appendTo($outputs);			
					
            $outputs = $("<span class='gridnic-pager-output' />").appendTo($container);
			$("<span class='ui-icon ui-output-excel' title='Excel' />").click(outputExcel).appendTo($outputs);			

            $outputs = $("<span class='gridnic-pager-output' />").appendTo($container);
			$("<span class='ui-icon ui-output-print' title='Print' />").click(outputPrint).appendTo($outputs);			

            $container.children().wrapAll("<div class='gridnic-pager' />");
        }
		
		function outputPrint()
		{
			output("print");
		}
		function outputExcel()
		{
			output("excel");
		}
		function outputWord()
		{
			output("word");
		}
		function outputPdf()
		{
			output("pdf");
		}
		
        function updatePager(pagingInfo)
        {
            var state = getNavState();

            $container.find(".gridnic-pager-nav span").removeClass("ui-state-disabled");
            if (!state.canGotoFirst) $container.find(".ui-icon-seek-first").addClass("ui-state-disabled");
            if (!state.canGotoLast) $container.find(".ui-icon-seek-end").addClass("ui-state-disabled");
            if (!state.canGotoNext) $container.find(".ui-icon-seek-next").addClass("ui-state-disabled");
            if (!state.canGotoPrev) $container.find(".ui-icon-seek-prev").addClass("ui-state-disabled");


            if (pagingInfo.pageSize == 0)
                $status.text("Showing all " + pagingInfo.totalRows + " rows");
            else
                $status.text("Showing page " + (pagingInfo.pageNum+1) + " of " + (Math.floor(pagingInfo.totalRows/pagingInfo.pageSize)+1));
        }



        init();
    }

    // GridNic.Controls.Pager
    $.extend(true, window, { GridNic: { Controls: { Pager: GridNicGridPager }}});
})();

