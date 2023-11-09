include_directories(${CMAKE_CURRENT_SOURCE_DIR} ${CMAKE_CURRENT_SOURCE_DIR}/include
			${OPENSSL_INCLUDE_DIR} ${STDBOOL_H_PATH} ${STDINT_H_PATH})
link_directories(${CMAKE_CURRENT_SOURCE_DIR})

add_library(mosquitto_external_auth MODULE mosquitto_external_auth.c)
set_target_properties(mosquitto_external_auth PROPERTIES
	POSITION_INDEPENDENT_CODE 1
)
set_target_properties(mosquitto_external_auth PROPERTIES PREFIX "")
target_link_libraries(mosquitto_external_auth curl)
if(WIN32)
	target_link_libraries(mosquitto_external_auth mosquitto curl)
endif(WIN32)

# Don't install, these are example plugins only.
#install(TARGETS mosquitto_external_auth RUNTIME DESTINATION "${CMAKE_INSTALL_BINDIR}" LIBRARY DESTINATION "${CMAKE_INSTALL_LIBDIR}")
