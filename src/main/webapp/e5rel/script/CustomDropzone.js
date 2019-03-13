
var CustomDropzone = Class.create();

CustomDropzone.prototype = (new Rico.Dropzone()).extend( {

   initialize: function( htmlElement, header, acceptRange ) {
      this.htmlElement  = $(htmlElement);
      this.header       = $(header);
      this.absoluteRect = null;
//      this.from = acceptRange[0];
//      this.to   = acceptRange[1];
		this.acceptRange = acceptRange;
      this.acceptedObjects = [];

      this.offset = navigator.userAgent.toLowerCase().indexOf("msie") >= 0 ? 0 : 1;
   },

   activate: function() {
      new Rico.Effect.FadeTo( this.htmlElement, .5, 250, 4 );
   },

   deactivate: function() {
      new Rico.Effect.FadeTo( this.htmlElement, 1, 250, 4 );
   },

   showHover: function() {
      if ( this.showingHover )
         return;
      this.header.style.color = "#000000";
      new Rico.Effect.FadeTo( this.htmlElement, .1, 250, 4 );
      this.showingHover = true;
   },

   hideHover: function() {
      if ( !this.showingHover )
         return;
      this.header.style.color = "#5b5b5b";
      new Rico.Effect.FadeTo( this.htmlElement, .5, 250, 4 );
      this.showingHover = false;
   },

   accept: function(draggableObjects) {

	var htmlElement = this.getHTMLElement();
      if ( htmlElement == null )
         return;

      n = draggableObjects.length;
      for ( var i = 0 ; i < n ; i++ )
      {
         var theGUI = draggableObjects[i].getDroppedGUI();
         if ( RicoUtil.getElementsComputedStyle( theGUI, "position" ) == "absolute" )
         {
            theGUI.style.position = "static";
            theGUI.style.top = "";
            theGUI.style.top = "";
         }
         htmlElement.appendChild(theGUI);
      }
   },

   canAccept: function(draggableObjects) {
      for ( var i = 0 ; i < draggableObjects.length ; i++ ) {
         if ( draggableObjects[i].type != "Custom" )
            return false;
         var kind = draggableObjects[i].kind;

         if ( kind.indexOf(this.acceptRange) < 0 )
            return false;
      }

      return true;
   }

} );