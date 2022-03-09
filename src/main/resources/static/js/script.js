$(".owl-carousel").owlCarousel({
    autoplay: true,
    autoplayHoverPause: true,
    autoplayTimeout: 2000,
    items: 4,
    loop: true,
    lazyLoad: true,
    responsive: {
        0 : {
            items: 2,
            dots: false,
            autoplay: false
        },
        768 : {
            items: 3,
            dots: false
        },
        1000: {
            items: 4,
            dots: true
        }
    }
});