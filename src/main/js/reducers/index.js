import {combineReducers} from "redux";
import auth from "./auth";
import message from "./message";
import professors from "./professors";

export default combineReducers({
    auth,
    message,
    professors,
});