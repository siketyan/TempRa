setData(temp, hum, pres);

drawGraph("record");
drawGraph("hour");
drawGraph("day");
drawGraph("month");
drawGraph("year");

var protocol, addr;
if (location.hostname === "monitor.siketyan.me") {
  protocol = "wss://";
  addr ="/socket";
} else {
  protocol = "ws://";
  addr = ":{{SOCKET_PORT}}/";
}

var con = new WebSocket(protocol + location.hostname + addr);
con.onmessage = function(e) {
  var data = e.data.split(",");
  setData(data[0], data[1], data[2]);
};

function setData(temp, hum, pres) {
  var tempPeriod = temp.indexOf(".");
  $("#tl").text(temp.substring(0, tempPeriod));
  $("#ts").text(temp.substring(tempPeriod, 5) + " °C");
  
  var humPeriod = hum.indexOf(".");
  $("#hl").text(hum.substring(0, humPeriod));
  $("#hs").text(hum.substring(humPeriod, 5) + " %");
  
  var presPeriod = pres.indexOf(".");
  $("#pl").text(pres.substring(0, presPeriod));
  $("#ps").text(pres.substring(presPeriod, 7) + " hPa");
}

function selectFormat(type) {
  switch (type) {
    case "record": return labelFormatRecord;
    case "hour": return labelFormatHour;
    case "day": return labelFormatDay;
    case "month": return labelFormatMonth;
    case "year": return labelFormatYear;
  }
}

function drawGraph(type) {
  var apiReq = new XMLHttpRequest();
  apiReq.onreadystatechange = function() {
    if (this.readyState === 4 && this.status === 200) {
      if (this.response) {
        var format = selectFormat(type);
        var obj = {
          type: this.response.type,
          data: {
            labels: [],
            datasets: this.response.data.datasets
          },
          options: this.response.options
        };
        
        for (var i = 0; i < this.response.data.label1.length; i++) {
          obj.data.labels.push(
            format.replace("{{STR1}}", this.response.data.label1[i])
              .replace("{{STR2}}", this.response.data.label2[i])
          );
        }
        
        new Chart($("#graph-" + type), obj);
      }
    }
  };
  apiReq.open('GET', 'api.json?type=' + type, true);
  apiReq.responseType = 'json';
  apiReq.send(null);
}