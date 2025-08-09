package vn.ptit.userservice.service;

import vn.ptit.model.dto.response.BaseResponseDTO;
import vn.ptit.userservice.dto.request.AuthRequestDTO;

public interface AuthService {

    BaseResponseDTO authenticate(AuthRequestDTO requestDTO);
}
