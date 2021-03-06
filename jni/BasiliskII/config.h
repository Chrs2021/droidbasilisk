/* config.h.  Generated from config.h.in by configure.  */
/* config.h.in.  Generated from configure.in by autoheader.  */


/*
 *  Copyright (C) 2002-2010  The DOSBox Team
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */


/* Define if building universal (internal helper macro) */
/* #undef AC_APPLE_UNIVERSAL_BUILD */

/* Compiling on BSD */
/* #undef BSD */

/* Determines if the compilers supports always_inline attribute. */

// FIXME HT
// #define C_CORE_INLINE 1

// Modified by Gerald
#define C_ATTRIBUTE_ALWAYS_INLINE 1
// #define C_ATTRIBUTE_ALWAYS_INLINE 0

/* Determines if the compilers supports fastcall attribute. */
#define C_ATTRIBUTE_FASTCALL 1

/* Define to 1 to use inlined memory functions in cpu core */
/* #undef C_CORE_INLINE */

/* Define to 1 to enable internal debugger, requires libcurses */
/* #undef C_DEBUG */

/* Define to 1 if you want serial passthrough support (Win32, Posix and OS/2).
   */
/* #define C_DIRECTSERIAL 0 */

/* Define to 1 to use x86 dynamic cpu core */
/* #define C_DYNAMIC_X86 1 */

/* Define to 1 to use recompiling cpu core. Can not be used together with the
   dynamic-x86 core */
#define C_DYNREC 1

/* Define to 1 to enable floating point emulation */
#define C_FPU 1

/* Define to 1 to use a x86 assembly fpu core */
/* #define C_FPU_X86 1 */

/* Determines if the compilers supports attributes for structures. */
#define C_HAS_ATTRIBUTE 1

/* Determines if the compilers supports __builtin_expect for branch
   prediction. */
#define C_HAS_BUILTIN_EXPECT 1

/* Define to 1 if you have the mprotect function */
#define C_HAVE_MPROTECT 1

/* Define to 1 to enable heavy debugging, also have to enable C_DEBUG */
/* #undef C_HEAVY_DEBUG */

/* Define to 1 to enable IPX over Internet networking, requires SDL_net */
/* #undef C_IPX */

/* Define to 1 to enable internal modem support, requires SDL_net */
/* #undef C_MODEM */

/* Define to 1 to use opengl display output support */
/* #define C_OPENGL 0 */

/* Define to 1 to enable SDL_sound support */
/* #undef C_SDL_SOUND */

/* Define to 1 if you have setpriority support */
#define C_SET_PRIORITY 1

/* Define to 1 to enable screenshots, requires libpng */
/* #undef C_SSHOT */

/* The type of cpu this target has */
#define C_TARGETCPU ARMV4LE

/* Define to 1 to use a unaligned memory access */
/* #define C_UNALIGNED_MEMORY 1 */

/* define to 1 if you have XKBlib.h and X11 lib */
/* #undef C_X11_XKB */

/* libm doesn't include powf */
/* #undef DB_HAVE_NO_POWF */

/* struct dirent has d_type */
#define DIRENT_HAS_D_TYPE 1

/* environ can be included */
#define ENVIRON_INCLUDED 1

/* environ can be linked */
#define ENVIRON_LINKED 1

/* Define to 1 to use ALSA for MIDI */
/* #undef HAVE_ALSA */

/* Define to 1 if you have the <ddraw.h> header file. */
/* #undef HAVE_DDRAW_H */

/* Define to 1 if you have the <inttypes.h> header file. */
#define HAVE_INTTYPES_H 1

/* Define to 1 if you have the `asound' library (-lasound). */
/* #define HAVE_LIBASOUND 1 */

/* Define to 1 if you have the <memory.h> header file. */
#define HAVE_MEMORY_H 1

/* Define to 1 if you have the <netinet/in.h> header file. */
#define HAVE_NETINET_IN_H 1

/* Define to 1 if you have the <pwd.h> header file. */
#define HAVE_PWD_H 1

/* Define to 1 if you have the <stdint.h> header file. */
#define HAVE_STDINT_H 1

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the <strings.h> header file. */
#define HAVE_STRINGS_H 1

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if you have the <sys/socket.h> header file. */
#define HAVE_SYS_SOCKET_H 1

/* Define to 1 if you have the <sys/stat.h> header file. */
#define HAVE_SYS_STAT_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
#define HAVE_SYS_TYPES_H 1

/* Define to 1 if you have the <unistd.h> header file. */
#define HAVE_UNISTD_H 1

/* Compiling on GNU/Linux */
/* #define LINUX 1 */

/* Compiling on Mac OS X */
/* #undef MACOSX */

/* Compiling on OS/2 EMX */
/* #undef OS2 */

/* Name of package */
#define PACKAGE "dosbox"

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT ""

/* Define to the full name of this package. */
#define PACKAGE_NAME "dosbox"

/* Define to the full name and version of this package. */
#define PACKAGE_STRING "dosbox 0.74"

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME "dosbox"

/* Define to the home page for this package. */
#define PACKAGE_URL ""

/* Define to the version of this package. */
#define PACKAGE_VERSION "0.74"

/* The size of `int *', as computed by sizeof. */
#define SIZEOF_INT_P 4

/* The size of `unsigned char', as computed by sizeof. */
#define SIZEOF_UNSIGNED_CHAR 1

/* The size of `unsigned int', as computed by sizeof. */
#define SIZEOF_UNSIGNED_INT 4

/* The size of `unsigned long', as computed by sizeof. */
#define SIZEOF_UNSIGNED_LONG 4

/* for basilisk */
#define SIZEOF_SHORT 2
#define SIZEOF_INT 4
#define SIZEOF_LONG 4
#define SIZEOF_LONG_LONG 8
#define SIZEOF_VOID_P 4
#define SIZEOF_FLOAT 4
#define SIZEOF_DOUBLE 8

#define HAVE_LOFF_T 1
#define HAVE_CADDR_T 1
#define USE_SDL 1
#define USE_SDL_VIDEO 1
#define FPU_UAE 1
#define HAVE_CONFIG_H 1
#define HAVE_STRDUP 1

#define HAVE_CFMAKERAW 1
#define HAVE_PTHREADS 1
#define HAVE_SIGINFO_T 1
//#define WANT_JIT 1
//#define USE_JIT 1

//#define WORDS_BIGENDIAN 1

/* The size of `unsigned long long', as computed by sizeof. */
#define SIZEOF_UNSIGNED_LONG_LONG 8

/* The size of `unsigned short', as computed by sizeof. */
#define SIZEOF_UNSIGNED_SHORT 2

/* Define to 1 if you have the ANSI C header files. */
#define STDC_HEADERS 1

/* Define to 1 if your <sys/time.h> declares `struct tm'. */
/* #undef TM_IN_SYS_TIME */

/* Version number of package */
#define VERSION "0.74"

/* Define WORDS_BIGENDIAN to 1 if your processor stores words with the most
   significant byte first (like Motorola and SPARC, unlike Intel). */
#if defined AC_APPLE_UNIVERSAL_BUILD
# if defined __BIG_ENDIAN__
#  define WORDS_BIGENDIAN 1
# endif
#else
# ifndef WORDS_BIGENDIAN
/* #  undef WORDS_BIGENDIAN */
# endif
#endif

/* Define to empty if `const' does not conform to ANSI C. */
/* #undef const */

/* Define to `__inline__' or `__inline' if that's what the C compiler
   calls it, or to nothing if 'inline' is not supported under any name.  */
#ifndef __cplusplus
/* #undef inline */
#endif

/* Define to `unsigned int' if <sys/types.h> does not define. */
/* #undef size_t */

/* Define to `int` if you don't have socklen_t */
/* #undef socklen_t */

#if C_ATTRIBUTE_ALWAYS_INLINE
#define INLINE inline __attribute__((always_inline))
#else
#define INLINE inline
#endif

#if C_ATTRIBUTE_FASTCALL
#define DB_FASTCALL __attribute__((fastcall))
#else
#define DB_FASTCALL
#endif

#if C_HAS_ATTRIBUTE
#define GCC_ATTRIBUTE(x) __attribute__ ((x))
#else
#define GCC_ATTRIBUTE(x) /* attribute not supported */
#endif

#if C_HAS_BUILTIN_EXPECT
#define GCC_UNLIKELY(x) __builtin_expect((x),0)
#define GCC_LIKELY(x) __builtin_expect((x),1)
#else
#define GCC_UNLIKELY(x) (x)
#define GCC_LIKELY(x) (x)
#endif


#ifndef CONFIG_TYPES
#define CONFIG_TYPES 1

typedef         double     Real64;

#if SIZEOF_UNSIGNED_CHAR != 1
#  error "sizeof (unsigned char) != 1"
#else
  typedef unsigned char Bit8u;
  typedef   signed char Bit8s;
#endif

#if SIZEOF_UNSIGNED_SHORT != 2
#  error "sizeof (unsigned short) != 2"
#else
  typedef unsigned short Bit16u;
  typedef   signed short Bit16s;
#endif

#if SIZEOF_UNSIGNED_INT == 4
  typedef unsigned int Bit32u;
  typedef   signed int Bit32s;
#elif SIZEOF_UNSIGNED_LONG == 4
  typedef unsigned long Bit32u;
  typedef   signed long Bit32s;
#else
#  error "can't find sizeof(type) of 4 bytes!"
#endif

#if SIZEOF_UNSIGNED_LONG == 8
  typedef unsigned long Bit64u;
  typedef   signed long Bit64s;
#elif SIZEOF_UNSIGNED_LONG_LONG == 8
  typedef unsigned long long Bit64u;
  typedef   signed long long Bit64s;
#else
#  error "can't find data type of 8 bytes"
#endif

#if SIZEOF_INT_P == 4
  typedef Bit32u Bitu;
  typedef Bit32s Bits;
#else
  typedef Bit64u Bitu;
  typedef Bit64s Bits;
#endif

#endif // CONFIG_TYPES



// added by Gerald
#ifndef __EXCEPTIONS
// Iff -fno-exceptions, transform error handling code to work without it.
# define try      if (true)
# define catch(X) if (false)
//# define throw
#else
// Else proceed normally.
# define try      try
# define catch(X) catch(X)
#endif

#include <stdio.h>

#if 0
//Not correct for invocations like printf(message).
//correct for invocations like printf("format", variables)
#define printf(fmt, args... ) \
    fprintf( stdout, "%s:%d:\n    "fmt, __FILE__, __LINE__, ##args ); \
    fflush( stdout )
#endif

#define ANDROID_PLATFROM
#define ANDROID_DEBUG

#ifdef ANDROID_PLATFROM
#ifdef ANDROID_DEBUG
#undef printf
#include <android/log.h>
#include <string.h>
extern char __android_dbg_buf[];
#define ALOG_DEBUG(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_DEBUG,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)

#define ALOG_INFO(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_INFO,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)

#define ALOG_ERROR(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_ERROR,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)

#define ALOG_WARN(fmt, args...) \
    do { \
        snprintf(__android_dbg_buf, 512, "%s:%d:  ", __FILE__, __LINE__); \
        snprintf(__android_dbg_buf+strlen(__android_dbg_buf), 512-strlen(__android_dbg_buf), fmt, ##args); \
        __android_log_print(ANDROID_LOG_WARN,  "adosbox", "%s", __android_dbg_buf); \
    } while(0)
#define printf(fmt, args...) \
      ALOG_INFO(fmt, ##args)

#else // ANDROID_DEBUG else
#define ALOG_DEBUG(fmt, args...)

#define ALOG_INFO(fmt, args...)

#define ALOG_ERROR(fmt, args...)

#define ALOG_WARN(fmt, args...)

#endif// ANDROID_DEBUG end

#if 0
#define printf(fmt, args...) \
    printf("%s:%d:\n    ", __FILE__, __LINE__); \
    printf(fmt, ##args); \
    fflush(stdout)
#endif
#endif // ANDROID_LOG_DEBUG

//#define DEBUG 1

