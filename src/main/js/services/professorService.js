import http from "../http-common";

const getAll = () => {
    return http.get("/professors");
};

const get = id => {
    return http.get(`/professors/${id}`);
};

const create = data => {
    return http.post("/professors", data);
};

const update = (id, data) => {
    return http.put(`/professors/${id}`, data);
};

const remove = id => {
    return http.delete(`/professors/${id}`);
};

const removeAll = () => {
    return http.delete(`/professors`);
};

const ProfessorService = {
    getAll,
    get,
    create,
    update,
    remove,
    removeAll
};

export default ProfessorService;