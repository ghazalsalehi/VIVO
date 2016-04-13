/*
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

var menuManagement = {

    // Initial page setup
    onLoad: function() {
        this.mergeFromTemplate();
        this.initMenuItemData();
        this.initObjects();
        this.initMenuItemsDD();
    },
    
    // Add variables from menupage template
    mergeFromTemplate: function() {
        $.extend(this, menuManagementData);
    },
    
    // Create references to frequently used elements for convenience
    initObjects: function() {
        this.menuItemsList = $('ul.menuItems');
    },
        
    // Drag-and-drop
    initMenuItemsDD: function() {
        var menuItems = this.menuItemsList.children('li');
        
        if (menuItems.length < 2) {
            return;
        }
        
        this.menuItemsList.addClass('dragNdrop');
        
        menuItems.attr('title', 'Drag and drop to reorder menu items');
        
        
        
           /*$("#loading").ajaxStart(function(){
               $(this).show();
             });
             $("#loading").ajaxStop(function(){
                $(this).hide();
              });*/

        
        this.menuItemsList.sortable({
            cursor: 'move',				
            update: function(event, ui) {
                menuManagement.reorderMenuItems(event, ui);
            }
        });
    },
    
    // Reorder menu items. Called after menu item drag-and-drop
    reorderMenuItems: function(event, ui) {
        var menuItems = $('li.menuItem').map(function(index, el) {
            return $(this).data('menuItemUri');
        }).get();
        
        
     

        $.ajax({
            url: menuManagement.reorderUrl,
            data: {
                predicate: menuManagement.positionPredicate,
                individuals: menuItems
            },
            traditional: true, // serialize the array of individuals for the server
            dataType: 'json',
            type: 'POST',
            success: function(data, status, request) {
                var pos;
                $('.menuItem').each(function(index){
                    pos = index + 1;
                    // Set the new position for this element. The only function of this value 
                    // is so we can reset an element to its original position in case reordering fails.
                    menuManagement.setPosition(this, pos);  
          
                });      
            },
            error: function(request, status, error) {
                // ui is undefined after removal of a menu item.
                if (ui) {
                    // Put the moved item back to its original position.
                    // Seems we need to do this by hand. Can't see any way to do it with jQuery UI. ??
                    var pos = menuManagement.getPosition(ui.item),
                        nextpos = pos + 1, 
                        menuItems = menuManagement.menuItemsList,
                        next = menuManagement.findMenuItem('position', nextpos);
                    
                    if (next.length) {
                        ui.item.insertBefore(next);
                    }
                    else {
                        ui.item.appendTo(menuItems);
                    }
                    
                    alert('Reordering of menu items failed.');
                }
            }
        });
    },
    
    // On page load, associate data with each menu item list element. Then we don't
    // have to keep retrieving data from or modifying the DOM as we manipulate the
    // menu items.
    initMenuItemData: function() {
        $('.menuItem').each(function(index) {
            $(this).data(menuItemData[index]);    
            
            // RY We might still need position to put back an element after reordering
            // failure. Position might already have been reset? Check.
            // We also may need position to implement undo links: we want the removed menu item
            // to show up in the list, but it has no position.
            $(this).data('position', index+1);      
        });
    },

    getPosition: function(menuItem) {
        return $(menuItem).data('position');
    },
    
    setPosition: function(menuItem, pos) {
        $(menuItem).data('position', pos);
    },
    
    findMenuItem: function(key, value) {
        var matchingMenuItem = $(); // if we don't find one, return an empty jQuery set
        
        $('.menuItem').each(function() {
            var menuItem = $(this);
            if ( menuItem.data(key) === value ) {
                matchingMenuItem = menuItem; 
                return false; // stop the loop
            }
        });
         
        return matchingMenuItem;       
    }
};

$(document).ready(function() {   
    menuManagement.onLoad();
}); 