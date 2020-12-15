import React from "react";
import axios from "axios";
import jwt_decode from "jwt-decode";
import Button from "@material-ui/core/Button";
import MaterialTable from "material-table";

export default function UnassignedDevices(props) {
  const [devices, setDevices] = React.useState([]);

  const callDevices = async () => {
    const data = await axios.get(
      global.BASE_URL + "/api/sensors/level/all/" + props.clickedLevel,
      {
        headers: {
          "Content-Type": "application/json;charset=UTF-8",
          Authorization: "Bearer " + localStorage.usertoken,
        },
      }
    );
    return data;
  };

  React.useEffect(() => {
    callDevices().then((res) => {
      console.log(res.data);
      setDevices(res.data);
    });
    //eslint-disable-next-line
  }, []);

  const columns = [
    {
      field: "id",
      title: "ID",
      hidden: true,
    },
    {
      field: "device_id",
      title: "Parent device ID",
    },
    {
      field: "sensor_type",
      title: "Sensor type",
    },
    {
      field: "node_id",
      title: "Mesh Address",
    },
    {
      field: "sensor_is_assigned",
      title: "Battery",
      editable: "never",
    },
    {
      field: "sensor_is_assigned",
      title: "Assigned?",
      editable: "never",
      render: (rowData) => {
        if (rowData.is_assigned === 0) return <span>No</span>;
        else return <span>Yes</span>;
      },
    },
  ];

  return (
    <MaterialTable
      columns={columns}
      onRowClick={(event, rowData) => {
        const row = devices[devices.indexOf(rowData)];
        console.log(row);
        props.assignSensor(row);
        setDevices(devices.filter((el) => el.id !== row.id));
      }}
      data={devices}
      title="Sensors"
    />
  );
}
