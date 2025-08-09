package vn.ptit.model.constants;

import vn.ptit.model.dto.response.BaseResponseDTO;

/**
 * Message in api response with internationalization (I18n) here <p>
 * Use when you want to create a detailed response message for the client in {@link BaseResponseDTO} <p>
 * The constant content corresponds to the message key in the resources bundle files in directories such as:
 * <ul>
 *   <li><a href="">resources/i18n/messages</a></li>
 * </ul>
 *
 * @author thoaidc
 */
@SuppressWarnings("unused")
public interface BaseResultConstants {

    // Get data success
    String GET_DATA_SUCCESS = "result.data.success";
    String SUCCESS = "result.success";

    // Get data failed
    String DATA_NOT_FOUND = "result.failed.data.notFound";
}
