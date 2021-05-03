import {
    CREATE_PROFESSOR,
    DELETE_ALL_PROFESSORS,
    DELETE_PROFESSOR,
    RETRIEVE_PROFESSORS,
    UPDATE_PROFESSOR,
} from "../actions/types";

const initialState = [];

function professorReducer(professors = initialState, action) {
    const {type, payload} = action;

    switch (type) {
        case CREATE_PROFESSOR:
            return [...professors, payload];

        case RETRIEVE_PROFESSORS:
            return payload;

        case UPDATE_PROFESSOR:
            return professors.map((professor) => {
                if (professor.id === payload.id) {
                    return {
                        ...professor,
                        ...payload,
                    };
                } else {
                    return professor;
                }
            });

        case DELETE_PROFESSOR:
            return professors.filter(({id}) => id !== payload.id);

        case DELETE_ALL_PROFESSORS:
            return [];

        default:
            return professors;
    }
}

export default professorReducer;