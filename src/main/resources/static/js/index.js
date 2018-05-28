$(document).ready(() => {
    $("#arrivalButton").click(registerCar);
    $("#arrivalStatus").click(arrivalStatusClicked);

    $("#departureButton").click(checkout);
    $("#departureStatus").click(departureStatusClicked);

    $("#cancelPayButton").click(hideTicket);
    $("#payButton").click(release);
});

function arrivalStatusClicked() {
    document.getElementById('arrivalStatus').style.visibility = 'hidden';
}
function departureStatusClicked() {
    document.getElementById('departureStatus').style.visibility = 'hidden';
}

function showArrivalStatus(success, text) {
    document.getElementById('departureStatus').style.visibility='hidden';
    let statusField = document.getElementById('arrivalStatus');
    statusField.style.visibility='hidden';
    if(success) {
        statusField.classList.remove("btn-danger");
        statusField.classList.add("btn-success");
    }
    else {
        statusField.classList.remove("btn-success");
        statusField.classList.add("btn-danger");
    }
    statusField.textContent=text;
    statusField.style.visibility='visible';
}

function showDepartureStatus(success, text) {
    document.getElementById('arrivalStatus').style.visibility='hidden';
    let statusField = document.getElementById('departureStatus');
    statusField.style.visibility='hidden';
    if(success) {
        statusField.classList.remove("btn-danger");
        statusField.classList.add("btn-success");
    }
    else {
        statusField.classList.remove("btn-success");
        statusField.classList.add("btn-danger");
    }
    statusField.textContent=text;
    statusField.style.visibility='visible';
}

function registerCar() {
    document.getElementById('arrivalStatus').style.visibility='hidden';
    document.getElementById('departureStatus').style.visibility='hidden';
    let textInput = $("#arrivalPlate")
    let text = textInput.val();
    textInput.val("");
    if (text.length === 0)
        return;
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "register",
        data: JSON.stringify({ registrationPlate: text }),
        success: () => {
            showArrivalStatus(true, "Car " + text +" registered");
        },
        error: jqXHR => {
            switch (jqXHR.status) {
                case 400:
                    showArrivalStatus(false, "Car " + text +" already registered");
                    break;
                case 406:
                    showArrivalStatus(false, "Too long registartion plate");
                    break;
            }
        }
    });
}

function checkout() {
    let textInput = $("#departurePlate")
    let text = textInput.val();
    textInput.val("");
    if (text.length === 0)
        return;
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "checkout",
        data: JSON.stringify({ registrationPlate: text, currency: 'PLN' }),
        success: response => {
            showTicket(response);
        },
        error: () => {
            showDepartureStatus(false, "Invalid registration plate");
        }
    });
}

function showTicket(data) {
    document.getElementById('arrivalStatus').style.visibility='hidden';
    document.getElementById('departureStatus').style.visibility='hidden';
    document.getElementById('ticket').style.visibility='visible';
    $("#checkoutRegistration").text(data.registrationPlate);
    $("#checkoutArrival").text(moment(data.arrival).format("YYYY-MM-DD HH:mm:ss"));
    $("#checkoutDeparture").text(moment(data.departure).format("YYYY-MM-DD HH:mm:ss"));
    $("#checkoutFee").text(data.fee + " "  + data.currency);
}

function hideTicket() {
    document.getElementById('ticket').style.visibility='hidden';
}

function release() {
    hideTicket();
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "unregister",
        data: JSON.stringify({
            registrationPlate: $("#checkoutRegistration").text(),
            departure: $("#checkoutDeparture").text()
        }),
        success: () => {
            showDepartureStatus(true, "Departure of "+$("#checkoutRegistration").text() + " registered");
        }
    });
}




