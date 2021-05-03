import {
    CREATE_PROFESSOR,
    DELETE_ALL_PROFESSORS,
    DELETE_PROFESSOR,
    RETRIEVE_PROFESSORS,
    UPDATE_PROFESSOR,
} from "./types";

import ProfessorDataService from "../services/professorService";

export const createProfessor = (title, description) => async (dispatch) => {
    try {
        const res = await ProfessorDataService.create({title, description});

        dispatch({
            type: CREATE_PROFESSOR,
            payload: res.data,
        });

        return Promise.resolve(res.data);
    } catch (err) {
        return Promise.reject(err);
    }
};

export const retrieveProfessors = () => async (dispatch) => {
    try {
        const res = await ProfessorDataService.getAll();

        dispatch({
            type: RETRIEVE_PROFESSORS,
            payload: res.data,
        });
    } catch (err) {
        console.log(err);
    }
};

export const updateProfessor = (id, data) => async (dispatch) => {
    try {
        const res = await ProfessorDataService.update(id, data);

        dispatch({
            type: UPDATE_PROFESSOR,
            payload: data,
        });

        return Promise.resolve(res.data);
    } catch (err) {
        return Promise.reject(err);
    }
};

export const deleteProfessor = (id) => async (dispatch) => {
    try {
        await ProfessorDataService.remove(id);

        dispatch({
            type: DELETE_PROFESSOR,
            payload: {id},
        });
    } catch (err) {
        console.log(err);
    }
};

export const deleteAllProfessors = () => async (dispatch) => {
    try {
        const res = await ProfessorDataService.removeAll();

        dispatch({
            type: DELETE_ALL_PROFESSORS,
            payload: res.data,
        });

        return Promise.resolve(res.data);
    } catch (err) {
        return Promise.reject(err);
    }
};