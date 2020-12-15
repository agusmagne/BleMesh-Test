import React from "react";
import Draggable from "react-draggable";
import {makeStyles} from "@material-ui/core/styles";
import Popover from "@material-ui/core/Popover";
import Typography from "@material-ui/core/Typography";
import {
  IconButton,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
  Icon,
} from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  typography: {
    padding: theme.spacing(2),
    width: "100%",
  },
  "@keyframes blinker": {
    "0%": {opacity: "0.2"},
    "20%": {opacity: "0.5"},
    "50%": {opacity: "1"},
    "80%": {opacity: "0.5"},
    "100%": {opacity: "0.2"},
  },
  blink: {
    color: "#3F51B5",
    animationName: "$blinker",
    animationDuration: "1.5s",
    animationTimingFunction: "linear",
    animationIterationCount: "infinite",
  },
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    width: "40vw",
  },
  card: {
    float: "left",
    width: "100%",
    maxHeight: "60px",
    margin: "auto",
    transition: "0.5s",
    padding: "5px",
    borderTopLeftRadius: "10px",
    borderTopRightRadius: "10px",
    backgroundColor: "rgba(161,161,161,0.2)",
    color: "#464646",
  },
  heading: {
    float: "left",
    paddingLeft: "5px",
    paddingTop: "4px",
    textAlign: "center",
    margin: "auto",
    backgroundColor: "#3452B4",
    borderRadius: "100px",
    width: "50px",
    height: "50px",
    color: "white",
    marginRight: "5px",
  },
  changeButton: {
    display: "inline-block",
    margin: "5px",
  },
}));

export default function LightNodes(props) {
  const classes = useStyles();
  const [activeDrags, setActiveDrags] = React.useState(0);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [openedPopoverId, setOpenedPopoverId] = React.useState(null);
  const [devices_, setDevices] = React.useState([]);

  const handleDrag = (e, ui, index, id) => {
    let {devices} = props;
    devices[index].fp_coordinates_bot =
      devices[index].fp_coordinates_bot + ui.deltaY;
    devices[index].fp_coordinates_left =
      devices[index].fp_coordinates_left + ui.deltaX;
    setDevices(devices);
    console.log(devices);
  };

  const onStart = () => {
    setActiveDrags(activeDrags + 1);
  };

  const onStop = () => {
    setActiveDrags(activeDrags - 1);
  };

  const handleClose = () => {
    setAnchorEl(null);
    setOpenedPopoverId(null);
  };

  const handleHover = (event, deviceId) => {
    setAnchorEl(event.currentTarget);
    setOpenedPopoverId(deviceId);
  };

  const open = Boolean(anchorEl);
  const id = open ? "simple-popover" : undefined;
  const dragHandlers = {onStart: onStart, onStop: onStop};

  const devices = props.devices;

  return (
    <div>
      {devices.length > 0 ? (
        devices.map((el, index) => {
          let color = "grey";
          let blink;
          if (el.status) {
            blink = el.status.includes("Battery powered")
              ? classes.blink
              : null;
            if (el.status.includes("OK")) color = "#4fa328";
            if (el.status.includes("No connection to driver")) color = "orange";
            if (
              el.status.includes("Not tested") ||
              el.status.includes("Not tested")
            )
              color = "#F50158";
            if (el.status.includes("Battery disconnected")) color = "purple";
            if (el.status.includes("Lamp fault")) color = "orange";
          }

          // switch (el.status) {
          //   case "OK":
          //     color = "#4fa328";
          //     break;
          //   case "No connection to driver":
          //     color = "orange";
          //     break;
          //   case "Battery powered/under test":
          //     color = "blue";
          //     setInterval(() => {
          //       color = "grey";
          //     }, 1000);
          //     setInterval(() => {
          //       color = "blue";
          //     }, 2000);
          //     break;
          //   case "Not tested":
          //     color = "#F50158";
          //     break;
          //   case "Battery disconnected":
          //     color = "purple";
          //     break;
          //   default:
          //     color = "grey";
          //     break;
          // }

          return (
            <Draggable
              key={el.id}
              position={{
                x: el.fp_coordinates_left,
                y: el.fp_coordinates_bot,
              }}
              onDrag={(e, ui) => handleDrag(e, ui, index, el.id)}
              {...dragHandlers}
              onStart={onStart}
              onStop={onStop}
              grid={[5, 5]}
              bounds={"parent"}
            >
              <div
                style={{
                  width: "0px",
                  height: "0px",
                  position: "relative",
                }}
              >
                <Icon
                  className={blink}
                  style={{
                    fontSize: "4em",
                    position: "absolute",
                    cursor: "move",
                    color: color,
                  }}
                  onClick={(e) => props.openContextMenu(e, el.id)}
                  onMouseEnter={(e) => handleHover(e, el.id)}
                  onMouseLeave={handleClose}
                  onTouchStart={(e) => handleHover(e, el.id)}
                  onTouchEnd={props.handleClose}
                >
                  location_on
                </Icon>
                {/* )} */}

                <Dialog
                  open={props.openedContextMenu === el.id}
                  onClose={props.handleCloseContextMenu}
                  aria-labelledby="form-dialog-title"
                >
                  <DialogTitle id="form-dialog-title">
                    {`${el.device_id} - ${el.type} - ${el.node_id}`}
                  </DialogTitle>

                  <DialogActions>
                    <Button onClick={() => props.checkConnectivity(el.id)}>
                      test connectivity
                    </Button>
                    <Button
                      onClick={props.handleCloseContextMenu}
                      color="primary"
                    >
                      close
                    </Button>
                  </DialogActions>
                </Dialog>

                {props.activeDrags < 1 ? (
                  <Popover
                    id={id}
                    open={openedPopoverId === el.id}
                    anchorEl={anchorEl}
                    style={{pointerEvents: "none"}} //important
                    anchorOrigin={{
                      vertical: "bottom",
                      horizontal: "center",
                    }}
                    transformOrigin={{
                      vertical: "top",
                      horizontal: "center",
                    }}
                  >
                    <div>
                      <Typography className={classes.typography}>
                        {`${el.device_id} - ${el.type} - ${el.node_id}`}
                      </Typography>
                    </div>
                    <div>
                      <Typography className={classes.typography}>
                        Status: {el.status}
                      </Typography>
                    </div>
                  </Popover>
                ) : null}
              </div>
            </Draggable>
          );
        })
      ) : (
        <Typography variant="h4" gutterBottom>
          No devices assigned
        </Typography>
      )}
    </div>
  );
}
