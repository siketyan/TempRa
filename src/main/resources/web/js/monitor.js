var periods = [
  "Now", "", "", "", "", "", "", "","", "",
  "10s", "", "", "", "", "", "", "","", "",
  "20s", "", "", "", "", "", "", "","", "",
  "30s", "", "", "", "", "", "", "","", "",
  "40s", "", "", "", "", "", "", "","", "",
  "50s", "", "", "", "", "", "", "","", "",
  "1m"
];

var template = {
  type: "line",
  data: {
    labels: [],
    datasets: [
      {
        borderColor: "rgb(255, 99, 132)",
        borderWidth: 1,
        data: [],
        fill: false,
        yAxisID: "axis-temp"
      },
      {
        borderColor: "rgb(54, 162, 235)",
        borderWidth: 1,
        data: [],
        fill: false,
        yAxisID: "axis-hum"
      },
      {
        borderColor: "rgb(255, 205, 86)",
        borderWidth: 1,
        data: [],
        fill: false,
        yAxisID: "axis-pres"
      }
    ]
  },
  options: {
    legend: {
      display: false
    },
    scales: {
      yAxes: [
        {
          scaleLabel: {
            display: true,
            labelString: "Temperature (°C)"
          },
          type: "linear",
          display: true,
          position: "left",
          id: "axis-temp"
        },
        {
          scaleLabel: {
            display: true,
            labelString: "Humidity (%)"
          },
          type: "linear",
          display: true,
          position: "right",
          id: "axis-hum"
        },
        {
          scaleLabel: {
            display: true,
            labelString: "Pressure (hPa)"
          },
          type: "linear",
          display: true,
          position: "right",
          id: "axis-pres"
        }
      ]
    }
  }
};

var charts = {};
drawGraphs();

getJSON(
  "",
  function (res) {
    setData(res.temp, res.hum, res.pres);

    var port = res.port;
    var protocol, addr;
    if (location.hostname === "monitor.siketyan.me") {
      protocol = "wss://";
      addr ="/socket";
    } else {
      protocol = "ws://";
      addr = ":" + port + "/";
    }

    var con = new WebSocket(protocol + location.hostname + addr);
    con.onmessage = function (e) {
      if (e.data === "update") {
        updateGraphs();
      } else {
        var data = e.data.split(",");
        setData(data[0], data[1], data[2]);

        var chart = charts["realtime"];
        chart.data.datasets[0].data.push(data[0]);
        chart.data.datasets[1].data.push(data[1]);
        chart.data.datasets[2].data.push(data[2]);

        var length = chart.data.datasets[0].data.length;
        if (length > 61) {
          chart.data.datasets[0].data.shift();
          chart.data.datasets[1].data.shift();
          chart.data.datasets[2].data.shift();
        } else {
          chart.data.labels.unshift(periods[length - 1]);
          chart.data.labels.pop();
        }

        chart.update();
      }
    };
  }
);

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

function updateGraphs() {
  updateGraph("record");
  updateGraph("hour");
  updateGraph("day");
  updateGraph("month");
  updateGraph("year");
}

function updateGraph(type) {
  getJSON(
    "?type=" + type,
    function (res) {
      var chart = charts[type];
      var format = res.format;
      chart.data.datasets[0].data = res.data[0];
      chart.data.datasets[1].data = res.data[1];
      chart.data.datasets[2].data = res.data[2];
      chart.data.labels = [];

      addLabels(type, format, res.label1, res.label2, chart.data.labels);
      chart.update();
    }
  );
}

function drawGraphs() {
  drawGraph("realtime");
  drawGraph("record");
  drawGraph("hour");
  drawGraph("day");
  drawGraph("month");
  drawGraph("year");
}

function drawGraph(type) {
  var obj = $.extend(true, {}, template);
  if (type === "realtime") {
    obj.data.labels = new Array(61)
    obj.options.animation = {
      duration: 0
    };
    obj.options.scales.xAxes = [
      {
        ticks: {
          autoSkip: false
        }
      }
    ];

    charts[type] = new Chart($("#graph-realtime"), obj);
  } else {
    getJSON(
      "?type=" + type,
      function (res) {
        var format = res.format;
        obj.data.datasets[0].data = res.data[0];
        obj.data.datasets[1].data = res.data[1];
        obj.data.datasets[2].data = res.data[2];

        addLabels(type, format, res.label1, res.label2, obj.data.labels);
        charts[type] = new Chart($("#graph-" + type), obj);
      }
    );
  }
}

function addLabels(type, format, labels1, labels2, labels) {
  for (var i = 0; i < labels1.length; i++) {
    var label1 = labels1[i];
    var label2 = labels2[i];
    if (type === "record" && label2 === 0) label2 = "00";

    labels.push(
      format.replace("{{STR1}}", String(label1))
            .replace("{{STR2}}", String(label2))
    );
  }
}

function getJSON(query, response) {
  var req = new XMLHttpRequest();
  req.onreadystatechange = function () {
    if (this.readyState === 4 && this.status === 200) {
      if (this.response) {
        response(this.response);
      }
    }
  };
  req.open('GET', 'api.json' + query, true);
  req.responseType = 'json';
  req.send(null);
}