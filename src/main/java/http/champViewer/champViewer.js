function loadAutoComplete() {
    let inputField = document.getElementById("tfChampname");
    let radbefore = inputField.style.borderBottomLeftRadius;
    fetchRest("/champion/list", "POST", {}, (res) => {
        $("#tfChampname").autocomplete({
            delay: 0,
            autoFocus: true,
            autofill: true,
            minchars: 1,
            source: res.champions,
            search: function(e, ui) {
                inputField.style.borderBottomRightRadius = "0px";
                inputField.style.borderBottomLeftRadius = "0px";
            },
            close: function(event, ui) {
                inputField.style.borderBottomRightRadius = radbefore;
                inputField.style.borderBottomLeftRadius = radbefore;
            },
            classes: {
                "ui-autocomplete": "highlight"
            }
        });
    });
}
loadAutoComplete();

const tfChampion = document.getElementById("tfChampname");

var fieldRed = false;

function errInputField(strMSG) {
    let colorBef = tfChampion.style.borderColor;
    tfChampion.style.borderColor = "var(--flatred)";
    if (fieldRed) return;
    fieldRed = true;
    setTimeout(() => {
        fieldRed = false;
        tfChampion.style.borderColor = colorBef;
    }, 1000);
    alert(strMSG);
}

function loadChampion() {
    if (tfChampion.value.length < 2) {
        errInputField("Champion name too short!");
        return;
    }
    fetchRest("/champion/singlelog", "POST", {
        "name": tfChampion.value
    }, (res) => {
        if ("ERROR" in res) {
            errInputField(res.ERROR);
            return;
        }

        let log = res.log;
        let data = res.data;

        let imgURL = `http://ddragon.leagueoflegends.com/cdn/img/champion/tiles/${log.name}_0.jpg`;
        tfChampion.value = "";
        $("#champImage").attr("src", imgURL);
        $("#champName").html(log.name);
        $("#runeName").html("Best rune: " + log.rune);
        $("#champview").css("visibility", "visible");
    });
}

function importRune() {
    fetchRest("/lol/setrune", "POST", {
        "name": document.getElementById("champName").innerHTML
    });
}