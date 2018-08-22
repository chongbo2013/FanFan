# For more information about using CMake with Android Studio, read the documentation
# https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

#   语法为CMAKE_MINIMUM_REQUIRED(VERSION versionNumber [FATAL_ERROR])
cmake_minimum_required(VERSION 3.4.1)

#   指定头文件的搜索路径，相当于指定gcc的-I参数
include_directories(opencv/include
                    src/main/cpp/include)

#将imgproc.cxx编译成静态库如libopencv_imgproc.a
add_library(opencv_imgproc STATIC IMPORTED)

#将core.cxx编译成静态库如libopencv_core.a
add_library(opencv_core STATIC IMPORTED)

#   SET指令将cmake模块路径设置一下
set_target_properties(opencv_imgproc PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libopencv_imgproc.a)
set_target_properties(opencv_core PROPERTIES IMPORTED_LOCATION ${PROJECT_SOURCE_DIR}/opencv/lib/${ANDROID_ABI}/libopencv_core.a)


# Creates and names a library, sets it as either STATIC or SHARED, and provides the relative paths to its source code.
#             创建并命名库，将其设置为静态的或共享，并提供其源代码的相对路径。
# You can define multiple libraries, and CMake builds them for you. 你可以定义多个库，和CMake建立他们对你。
# Gradle automatically packages shared libraries with your APK.     Gradle自动包共享库与你的apk。


add_library( # Sets the name of the library.    设置库的名称
             smart_cropper

             # Sets the library as a shared library.    将库设置为共享库
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/Scanner.cpp
             src/main/cpp/smart_cropper.cpp
             src/main/cpp/android_utils.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#   VAR变量表示找到的库全路径,包含库文件名
find_library( # Sets the name of the path variable. 设置PATH变量的名称
              log-lib

              # Specifies the name of the NDK library that  指定NDK库的名称
              # you want CMake to locate.
              log)

#   FIND_LIBRARY(<VAR>name1 path1 path2 …)  VAR变量表示找到的库全路径,包含库文件名
find_library(jnigraphics-lib
             jnigraphics)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#   添加链接库，相同于指定-l参数
target_link_libraries( # Specifies the target library.  指定目标库
                       smart_cropper
                       opencv_imgproc
                       opencv_core

                       # Links the target library to the log library    将目标库链接到日志库
                       # included in the NDK.
                       ${log-lib}
                       ${jnigraphics-lib})