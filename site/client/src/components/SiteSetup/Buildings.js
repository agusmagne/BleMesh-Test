import React from "react";
import NoDataIndication from "components/NoDataIndication/NoDataIndicationTable";
import "./table.css";
import MaterialTable from "material-table";
import axios from "axios";
import {useHistory} from "react-router-dom";

export default function Buildings(props) {
  const history = useHistory();

  const columns = [
    {
      field: "buildings_id",
      title: "ID",
      hidden: true,
    },
    {
      field: "building",
      title: "Building",
    },
    {
      field: "address",
      title: "Address",
    },
    {
      field: "devices",
      title: "Luminaires count",
      editable: "never",
    },
  ];

  const validate = (data) => {
    const values = Object.values(data);
    return !values.some((el) => el.length < 3);
  };

  const updateBuilding = async (updateData) => {
    let result = await axios({
      //Axios POST request
      method: "post",
      headers: {
        "Content-Type": "application/json;charset=UTF-8",
        Authorization: "Bearer " + localStorage.usertoken,
      },
      url: global.BASE_URL + "/api/buildings/" + updateData.buildings_id,
      data: {
        building: updateData.building,
        address: updateData.address,
      },
      timeout: 0,
    });
    return result;
  };

  const addBuilding = async (updateData) => {
    let result = await axios({
      //Axios POST request
      method: "post",
      headers: {
        "Content-Type": "application/json;charset=UTF-8",
        Authorization: "Bearer " + localStorage.usertoken,
      },
      url: global.BASE_URL + "/api/buildings/new-empty",
      data: {
        building: updateData.building,
        address: updateData.address,
        sites_id: props.clickedSite,
      },
      timeout: 0,
    });
    return result;
  };

  const deleteBuilding = async (data) => {
    let result = await axios({
      //Axios POST request
      method: "delete",
      headers: {
        "Content-Type": "application/json;charset=UTF-8",
        Authorization: "Bearer " + localStorage.usertoken,
      },
      url: global.BASE_URL + "/api/buildings/" + data.buildings_id,
      timeout: 0,
    });
    return result;
  };

  let editable = {
    onRowAdd: (newData) =>
      new Promise((resolve, reject) => {
        console.log(newData);
        if (validate(newData))
          addBuilding(newData).then((res) => {
            console.log(res);
            props.handleEditBuilding(newData, {}, "add");
            resolve();
          });
        else reject();
      }),
    onRowUpdate: (newData, oldData) =>
      new Promise((resolve, reject) => {
        if (validate(newData))
          updateBuilding(newData).then((res) => {
            console.log(res);
            props.handleEditBuilding(newData, oldData, "update");

            resolve();
          });
        else reject();
      }),
    // onRowDelete: (oldData) =>
    //   new Promise((resolve, reject) => {
    //     deleteBuilding(oldData).then((res) => {
    //       props.handleEditBuilding({}, oldData, "delete");
    //       resolve();
    //     });
    //   }),
  };

  let actions = [
    {
      icon: "note_add",
      tooltip: "Upload CSV",
      onClick: (event, rowData) => {
        history.push({
          pathname: "/admin/upload-csv",
          state: {building: rowData, site: props.clickedSite},
        });
      },
    },
  ];

  if (!props.editable) {
    actions = [];
    editable = {};
  }
  console.log(props.buildings);
  return (
    <MaterialTable
      columns={columns}
      onRowClick={props.handleClickBuilding}
      data={props.buildings}
      title="Buildings"
      editable={editable}
      actions={actions}
    />
  );
}
