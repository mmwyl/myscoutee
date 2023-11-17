cmake_minimum_required(VERSION 3.27)
project(nginx)

set(CMAKE_MESSAGE_LOG_LEVEL "DEBUG")

option(NGX_THREADS "Enable Nginx threads" ON)

function(include_directories_recursive root_dir)
    file(GLOB_RECURSE DIRS LIST_DIRECTORIES true ${root_dir}/*)
    #message(STATUS "Included root directory: ${root_dir}")
    foreach(dir ${DIRS})
    #AND NOT "${dir}" MATCHES "win32" AND NOT "${dir}" MATCHES "modules"
        if(IS_DIRECTORY ${dir})
            include_directories(${dir})
            #message(STATUS "Included directory: ${dir}")
        endif()
    endforeach()
endfunction()

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/objs)
include_directories_recursive(${CMAKE_CURRENT_SOURCE_DIR}/src)

set(NGINX_SOURCE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/src)

file(GLOB_RECURSE NGINX_SOURCES 
    "${NGINX_SOURCE_PATH}/*.c"
)

#list(FILTER NGINX_SOURCES EXCLUDE REGEX "/win32/*.c")
#list(FILTER NGINX_SOURCES EXCLUDE REGEX "/modules/*.c")
list(APPEND NGINX_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/objs/ngx_modules.c")

foreach(dir ${NGINX_SOURCES})
    #message(STATUS "Included files: ${dir}")
endforeach()


add_executable(nginx ${NGINX_SOURCES})
