weibo = window.weibo || {
  upload: function (o) {
      var hashstr = $("#wb_hash").val();
      var hml = ' ';
      hml += '<form method="post" id="uploadform" action="cp.php?ac=wbupload&hash='+hashstr+'" enctype="multipart/form-data"><a href="javascript:void(0);" class="choose_pic"><span>选择本地图片</span><input class="input_f" id="wbpic" name="wbpic" type="file" onchange="weibo.upload_pic()"></a></form><div>仅支持JPG、GIF、PNG、JPEG图片，且小于3MB</div>';
      var html = '<div id="ppop_box" class="pop_box"><div class="p-arrow"><em>◆</em><span>◆</span></div><div class="b_hd"><a href="javascript:void(0);" class="p_close"></a>上传图片</div><div class="b_bd">';
      html += hml + '</div></div>';
      if( $('#ppop_box').html()==null ){
          $('body').append(html);
      }
      var position = $(o).offset();
      $('#ppop_box').css({"top":position.top+"px","left":position.left-($("#ppop_box").width()/2)+98+"px","display":"none"});
      $('#ppop_box').fadeIn("fast");
      $("#ppop_box .p_close").one('click',function(){
          $("#ppop_box").hide();
          updatehash();
      });
  },
   
  upload_pic:  function () {
    var pic = $('#wbpic').val();
    if(!/\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test(pic)){
        alert("只能上传gif,jpeg,jpg,png格式图片！")
        return false;
      }
    var options = {
        global:false,
        beforeSend: function(){
            $('<div class="boxy-modal-blackout"></div>').css($.extend(ui.box._cssForOverlay(), {
                zIndex: 9999, opacity: 0.1
            })).appendTo(document.body);
            ui.load('图片正在上传，请稍后...');
        },
        success:function(s){
            if(s.picid == '' || s.error){
                if(s.error){
                  alert('上传图片最大不能超过3MB!');
                }else{
                  alert('图片上传失败!');
                }
              $('.boxy-modal-blackout').remove();
              ui.loaded();
              return false;
            }else{
                $('#wb_images').val(s.url);
                $('#wb_picid').val(s.picid);
                $('#shuoshuoup').html(s.name+' <a href="javacript:;" onclick="deluploadpic()">删除</a>');
                $('#message').focus();
            }
            $('.boxy-modal-blackout').remove();
            ui.loaded();
            $('#ppop_box').hide();
        },
        dataType: "json"
    };
    $('#uploadform').ajaxSubmit( options );
    return false;
  }
   
};