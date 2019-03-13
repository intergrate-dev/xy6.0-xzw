/**
 * 幻灯片
 * Created by isaac_gu on 2016/2/26.
 */
(function(window, $, LE){
    LE.options["Carousel"] = {
        selector: "#carouselLi", //标签选择器
        tag: "plugin",
        viewHtml: [
            '<div class="le-carousel carousel slide plugin_entity plugin-hint" data-ride="carousel">' +
            '    <ol class="carousel-indicators">' +
            '        <li data-target="#" data-slide-to="0" class="active"></li>' +
            '        <li data-target="#" data-slide-to="1" class=""></li>' +
            '        <li data-target="#" data-slide-to="2" class=""></li>' +
            '    </ol>' +
            '    <div class="carousel-inner" role="listbox">' +
            '        <div class="item active">' +
            '<a href="#">' +
            '            <img data-src="holder.js/900x500/auto/#777:#777" alt="900x500" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true">' +
            '            <div class="carousel-caption">' +
            '                <h4>请点击右侧“添加稿件”按钮</h4>' +
            '                <p>Nulla vitae elit libero, a pharetra augue mollis interdum.</p>' +
            '            </div>' +
            '</a>' +
            '        </div>' +
            '        <div class="item">' +
            '<a href="#">' +
            '            <img data-src="holder.js/900x500/auto/#666:#666" alt="900x500" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNjY2OiM2NjYKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmYTkwMCB0ZXh0IHsgZmlsbDojNjY2O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWZhOTAwIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzY2NiIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true">' +
            '            <div class="carousel-caption">' +
            '                <h4>请点击右侧“添加稿件”按钮</h4>' +
            '                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>' +
            '            </div>' +
            '</a>' +
            '        </div>' +
            '        <div class="item">' +
            '<a href="#">' +
            '            <img data-src="holder.js/900x500/auto/#555:#555" alt="900x500" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNTU1OiM1NTUKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmYjI5NyB0ZXh0IHsgZmlsbDojNTU1O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWZiMjk3Ij48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzU1NSIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true">' +
            '            <div class="carousel-caption">' +
            '                <h4>请点击右侧“添加稿件”按钮</h4>' +
            '                <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur.</p>' +
            '            </div>' +
            '</a>' +
            '        </div>' +
            '    </div>' +
            '    <a class="left carousel-control" href="#" role="button" data-slide="prev">' +
            '        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>' +
            '        <span class="sr-only">Previous</span>' +
            '    </a>' +
            '    <a class="right carousel-control" href="#" role="button" data-slide="next">' +
            '        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>' +
            '        <span class="sr-only">Next</span>' +
            '    </a>' +
            '</div>'
        ].join(""),
        viewinnerHtml: [
            '    <ol class="carousel-indicators">' +
            '        <li data-target="#{target}" data-slide-to="0" class="active"></li>' +
            '        <li data-target="#{target}" data-slide-to="1" class=""></li>' +
            '        <li data-target="#{target}" data-slide-to="2" class=""></li>' +
            '    </ol>' +
            '    <div class="carousel-inner" role="listbox">' +
            '        <div class="item active">' +
            '<a href="#">' +
            '            <img data-src="holder.js/900x500/auto/#777:#777" alt="900x500" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNzc3OiM3NzcKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmODViZiB0ZXh0IHsgZmlsbDojNzc3O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWY4NWJmIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzc3NyIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true">' +
            '            <div class="carousel-caption">' +
            '                <h4>请点击右侧“添加稿件”按钮</h4>' +
            '                <p>Nulla vitae elit libero, a pharetra augue mollis interdum.</p>' +
            '            </div>' +
            '</a>' +
            '        </div>' +
            '        <div class="item">' +
            '<a href="#">' +
            '            <img data-src="holder.js/900x500/auto/#666:#666" alt="900x500" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNjY2OiM2NjYKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmYTkwMCB0ZXh0IHsgZmlsbDojNjY2O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWZhOTAwIj48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzY2NiIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true">' +
            '            <div class="carousel-caption">' +
            '                <h4>请点击右侧“添加稿件”按钮</h4>' +
            '                <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>' +
            '            </div>' +
            '</a>' +
            '        </div>' +
            '        <div class="item">' +
            '<a href="#">' +
            '            <img data-src="holder.js/900x500/auto/#555:#555" alt="900x500" src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI/PjxzdmcgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgdmlld0JveD0iMCAwIDkwMCA1MDAiIHByZXNlcnZlQXNwZWN0UmF0aW89Im5vbmUiPjwhLS0KU291cmNlIFVSTDogaG9sZGVyLmpzLzkwMHg1MDAvYXV0by8jNTU1OiM1NTUKQ3JlYXRlZCB3aXRoIEhvbGRlci5qcyAyLjYuMC4KTGVhcm4gbW9yZSBhdCBodHRwOi8vaG9sZGVyanMuY29tCihjKSAyMDEyLTIwMTUgSXZhbiBNYWxvcGluc2t5IC0gaHR0cDovL2ltc2t5LmNvCi0tPjxkZWZzPjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+PCFbQ0RBVEFbI2hvbGRlcl8xNTM5ZDVmYjI5NyB0ZXh0IHsgZmlsbDojNTU1O2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1mYW1pbHk6QXJpYWwsIEhlbHZldGljYSwgT3BlbiBTYW5zLCBzYW5zLXNlcmlmLCBtb25vc3BhY2U7Zm9udC1zaXplOjQ1cHQgfSBdXT48L3N0eWxlPjwvZGVmcz48ZyBpZD0iaG9sZGVyXzE1MzlkNWZiMjk3Ij48cmVjdCB3aWR0aD0iOTAwIiBoZWlnaHQ9IjUwMCIgZmlsbD0iIzU1NSIvPjxnPjx0ZXh0IHg9IjMzMy4yMTA5Mzc1IiB5PSIyNzAuMSI+OTAweDUwMDwvdGV4dD48L2c+PC9nPjwvc3ZnPg==" data-holder-rendered="true">' +
            '            <div class="carousel-caption">' +
            '                <h4>请点击右侧“添加稿件”按钮</h4>' +
            '                <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur.</p>' +
            '            </div>' +
            '</a>' +
            '        </div>' +
            '    </div>' +
            '    <a class="left carousel-control" href="#{target}" role="button" data-slide="prev">' +
            '        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>' +
            '        <span class="sr-only">Previous</span>' +
            '    </a>' +
            '    <a class="right carousel-control" href="#{target}" role="button" data-slide="next">' +
            '        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>' +
            '        <span class="sr-only">Next</span>' +
            '    </a>'
        ].join(""),
        liHtml: '<li data-target="#{target}" id="#{id}" data-slide-to="#{index}" class=""><span>#{num}</span></li>',
        divHtml: [

            '<div class="item" id="#{id}">' +
            '<a href="#{link}" target="_blank">' +
            '<img alt="" onerror="#{error}" width="100%" src="#{src}" data-holder-rendered="false">' +
            '<div class="carousel-caption">' +
            '<h4>#{title}</h4>' +
            '<p>#{summary}</p>' +
            '</div>' +
            '</a>' +
            '</div>'


        ].join("")
    };

    LE.plugins["Carousel"] = function(){
        var initContainer = function(){
            $("#container").on({
                click: function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    var options = {
                        object: $(this),
                        type: "all"
                    };
                    LEStyle.destroyAll($(this).attr("id"));
                    LEStyle.run("css-Position", options, true).run("css-BackGround", options, true).run("css-BolderSetting", options, true).run("css-TextSettingWhole", options, true).run("PicManage", options);
                }
            }, ".le-carousel");
        };
        return {
            init: function(){
                initContainer();
            },
            afterDrag: function(id){
                id = "#" + id;
                $(id).find('a').filter(".carousel-control").attr("href", id);
                $(id).find("ol").filter(".carousel-indicators").find("li").attr("data-target", id);
                $(id).carousel('cycle');
            },
            afterDragClone: function(id){
                id = "#" + id;
                $(id).find('a').filter(".carousel-control").attr("href", id);
                $(id).find("ol").filter(".carousel-indicators").find("li").attr("data-target", id);
                $(id).carousel('cycle');
            }
        }
    };

})(window, jQuery, LE, undefined);