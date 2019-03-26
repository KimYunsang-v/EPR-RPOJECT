import {combineReducers} from 'redux';
import userState from './userState'
import projectState from "./projectState";
import saveUpmu from "./saveUpmu";
// reducer 합치는곳 
export default combineReducers({
    userState, projectState, saveUpmu,
});
