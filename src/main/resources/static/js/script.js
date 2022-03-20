
const menuButton = $('#nav-menu-button');
const dropDown = $('#nav-drop-down');
const navMenu = $('#nav-menu');
const navContent = $('#nav-content');
const adminDrop = $('#admin-drop');
const adminDropDown = $('#admin-drop-down');

menuButton.click(() => {
    if (dropDown.hasClass('hidden'))
        dropDown.removeClass('hidden');
    else
        dropDown.addClass('hidden');
})

adminDrop.click(() => {
    if (adminDropDown.hasClass('hidden'))
        adminDropDown.removeClass('hidden');
    else
        adminDropDown.addClass('hidden');
})


$(document).on('click', function(event) {
    if (!$(event.target).closest('.menu-button').length) {
        // Hide the menus.
        if (!dropDown.hasClass('hidden'))
            dropDown.addClass('hidden');
        if (!adminDropDown.hasClass('hidden'))
            adminDropDown.addClass('hidden');
    }
});

navMenu.click(() => {
    if (navContent.hasClass('hidden'))
        navContent.removeClass('hidden');
    else
        navContent.addClass('hidden');
})
