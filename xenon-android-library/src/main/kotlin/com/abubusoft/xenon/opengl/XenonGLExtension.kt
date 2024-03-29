package com.abubusoft.xenon.opengl

enum class XenonGLExtension(val string: String, var isPresent: Boolean) {
    /**
     *
     *
     * Preso da [OES_texture_float](http://www.khronos.org/registry/gles/extensions/OES/OES_texture_float.txt).
     *
     * <dl>
     * <dt>Name</dt>
     * <dd>
     *
     * <pre>
     * OES_texture_half_float, OES_texture_float
    </pre> *
     *
    </dd> *
     * <dt>Name Strings</dt>
     * <dd>
     *
     * <pre>
     * GL_OES_texture_half_float, GL_OES_texture_float
    </pre> *
     *
    </dd> *
     * <dt>IP Status</dt>
     * <dd>
     *
     * <pre>
     * Please refer to the ARB_texture_float extension.
    </pre> *
     *
    </dd> *
     * <dt>Version</dt>
     * <dd>
     *
     * <pre>
     * Last Modified Date: November 9, 2011
    </pre> *
     *
    </dd> *
     * <dt>Number</dt>
     * <dd>
     *
     * <pre>
     * OpenGL ES Extension #36
    </pre> *
     *
    </dd> *
     * <dt>Dependencies</dt>
     * <dd>
     *
     * <pre>
     * This extension is written against the OpenGL ES 2.0 Specification.
     * OES_texture_3D affects the definition of this extension.
    </pre> *
     *
    </dd> *
     * <dt>Overview</dt>
     * <dd>
     *
     * <pre>
     * These extensions add texture formats with 16- (aka half float) and 32-bit
     * floating-point components.  The 32-bit floating-point components
     * are in the standard IEEE float format.  The 16-bit floating-point
     * components have 1 sign bit, 5 exponent bits, and 10 mantissa bits.
     * Floating-point components are clamped to the limits of the range
     * representable by their format.
     *
     * The OES_texture_half_float extension string indicates that the
     * implementation supports 16-bit floating pt texture formats.
     *
     * The OES_texture_float extension string indicates that the
     * implementation supports 32-bit floating pt texture formats.
     *
     * Both these extensions only require NEAREST magnification filter and
     * NEAREST, and NEAREST_MIPMAP_NEAREST minification filters to be supported.
    </pre> *
     *
    </dd> *
     * <dt>Issues</dt>
     * <dd>
     *
     * <pre>
     *
     * 1.  What should we do if magnification filter for a texture with half-float
     * or float channels is set to LINEAR.
     *
     * RESOLUTION: The texture will be marked as incomplete.
     * Only the NEAREST filter is supported.
     *
     * The cost of doing a LINEAR filter for these texture formats can be
     * quite prohibitive.  There was a discussion on having the shader
     * generate code to do LINEAR filter by making individual texture calls with a
     * NEAREST filter but again the computational and memory b/w costs decided
     * against mandating this approach.  The decision was that this extension
     * would only enable NEAREST magnification filter.  Support for LINEAR
     * magnification filter would be done through a separate extension.
     *
     * 2.  What should we do if minification filter is set to LINEAR or
     * LINEAR_MIPMAP_NEAREST, NEAREST_MIPMAP_LINEAR and LINEAR_MIPMAP_LINEAR.
     *
     * RESOLUTION: The texture will be marked as incomplete.  Only the NEAREST
     * and NEAREST_MIPMAP_NEAREST minification filters are supported.
     *
     * This was decided for the same reasons given in issue #1.  The decision
     * was that this extension would only enable NEAREST and NEAREST_MIPMAP_NEAREST
     * minification filters, and the remaining OpenGL ES minification filters
     * would be supported through a separate extension.
     *
     * 3.  Should CopyTexImage2D, CopyTexSubImage{2D|3D} be supported for textures
     * with half-float and float channels?
     *
     * RESOLUTION: No.
    </pre> *
     *
    </dd> *
     * <dt>New Tokens</dt>
     * <dd>
     *
     * <pre>
     * Accepted by the <allocation> parameter of TexImage2D,  TexSubImage2D, TexImage3D, and TexSubImage3D
     *
     * HALF_FLOAT_OES                0x8D61
     * FLOAT                         0x1406
     *
     * Additions to Chapter 2 of the OpenGL ES 2.0 Specification (OpenGL Operation)
     *
     * Add a new section called 16-Bit Floating-Point Numbers
     *
     * "A 16-bit floating-point number has a 1-bit sign (S), a 5-bit
     * exponent (E), and a 10-bit mantissa (M).  The value of a 16-bit
     * floating-point number is determined by the following:
     *
     * (-1)^S * 0.0,                        if E == 0 and M == 0,
     * (-1)^S * 2^-14 * (M / 2^10),         if E == 0 and M != 0,
     * (-1)^S * 2^(E-15) * (1 + M/2^10),    if 0 < E < 31,
     * (-1)^S * INF,                        if E == 31 and M == 0, or
     * NaN,                                 if E == 31 and M != 0,
     *
     * where
     *
     * S = floor((N mod 65536) / 32768),
     * E = floor((N mod 32768) / 1024), and
     * M = N mod 1024.
     *
     * Implementations are also allowed to use any of the following
     * alternative encodings:
     *
     * (-1)^S * 0.0,                        if E == 0 and M != 0,
     * (-1)^S * 2^(E-15) * (1 + M/2^10),    if E == 31 and M == 0, or
     * (-1)^S * 2^(E-15) * (1 + M/2^10),    if E == 31 and M != 0,
     *
     * Any representable 16-bit floating-point value is legal as input
     * to a GL command that accepts 16-bit floating-point data.  The
     * result of providing a value that is not a floating-point number
     * (such as infinity or NaN) to such a command is unspecified, but
     * must not lead to GL interruption or termination.  Providing a
     * denormalized number or negative zero to GL must yield predictable
     * results."
     *
     * Add to Table 2.2, p. 12:
     *
     * Minimum
     * GL Type  Bit Width  Description
     * -------  ---------  -----------------------------------
     * half     16         Half-precision floating-point value
     * encoded in an unsigned scalar
     *
     * Additions to Chapter 3 of the OpenGL ES 2.0 Specification (Rasterization)
     *
     * Add to Table 3.2, p. 62:
     *
     * allocation Parameter  Corresponding  Special
     * Token Name      GL Data Type   Interpretation
     * --------------  -------------  --------------
     * HALF_FLOAT_OES  half           No
     *
     * Modify first sentence of "Unpacking", p. 62:
     *
     * "Data are taken from client memory as a sequence of one of the GL data
     * types listed in Table 3.2. These elements are..."
     *
     * Add to Table 3.4, p. 63:
     *
     * Format           Type            Bytes per Pixel
     * ---------        --------------  ---------------
     * RGBA             FLOAT           16
     * RGB              FLOAT           12
     * LUMINANCE_ALPHA  FLOAT           8
     * LUMINANCE        FLOAT           4
     * ALPHA            FLOAT           4
     * RGBA             HALF_FLOAT_OES  8
     * RGB              HALF_FLOAT_OES  6
     * LUMINANCE_ALPHA  HALF_FLOAT_OES  4
     * LUMINANCE        HALF_FLOAT_OES  2
     * ALPHA            HALF_FLOAT_OES  2
     *
     * Modify fifth paragraph of 3.7.1 Texture Image Specification, p. 67:
     *
     * "The selected groups are processed as described in section 3.6.2, stopping
     * after final expansion to RGBA. If the internal format of the texture is
     * fixed-point, components are clamped to [0,1]. Otherwise, values are not
     * modified."
    </allocation></pre> *
     *
    </dd> *
     * <dt>Interactions with OES_texture_3D</dt>
     * <dd>
     *
     * <pre>
     * If OES_texture_3D is not supported, ignore references to TexImage3D and TexSubImage3D.
    </pre> *
     *
    </dd> *
     * <dt>Revision History</dt>
     * <dd>
     *
     * <pre>
     * 04/29/2005    0.1    Original draft.
     * 06/29/2005    0.2    Added issues on why only NEAREST and
     * NEAREST_MIPMAP_NEAREST filters are required.
     * 04/21/2006    0.3    Added TexSubImage2D and TexSubImage3D as
     * functions that take the new tokens.
     * 06/14/2006    0.4    CopyTexImage2D, CopyTexSubImag{2D|3D} are
     * not supported.
     * 07/26/2011    0.5    Fix several omissions discovered while writing
     * EXT_color_buffer_half_float.
     * 11/09/2011    0.6    Fix missing FLOAT entries in Table 3.4,
     * add interaction with OES_texture_3D.
    </pre> *
     *
    </dd> *
    </dl> *
     *
     */
    TEXTURE_HALF_FLOAT("GL_OES_texture_half_float", false),

    /**
     *
     *
     * Preso da [OES_texture_float](http://www.khronos.org/registry/gles/extensions/OES/OES_texture_float.txt).
     *
     *
     *
     * Vedi [.TEXTURE_HALF_FLOAT]
     *
     */
    TEXTURE_FLOAT("GL_OES_texture_float", false),

    /**
     *
     *
     * Preso da [OES_standard_derivatives](http://www.khronos.org/registry/gles/extensions/OES/OES_standard_derivatives.txt).
     *
     *
     * <dl>
     *
     * <dt>Name</dt>
     * <dd>
     *
     * <pre>
     * OES_standard_derivatives
    </pre> *
     *
    </dd> *
     * <dt>Name Strings</dt>
     * <dd>
     *
     * <pre>
     * GL_OES_standard_derivatives
    </pre> *
     *
    </dd> *
     * <dt>Version</dt>
     * <dd>
     *
     * <pre>
     * Date: July 18, 2007 Revision: 0.99
    </pre> *
     *
    </dd> *
     * <dt>Number</dt>
     * <dd>
     *
     * <pre>
     * OpenGL ES Extension #45
    </pre> *
     *
    </dd> *
     * <dt>Dependencies</dt>
     * <dd>
     *
     * <pre>
     * OpenGL ES 2.0 is required.
    </pre> *
     *
    </dd> *
     * <dt>Overview</dt>
     * <dd>
     *
     * <pre>
     * The standard derivative built-in functions and semantics from OpenGL 2.0 are
     * optional for OpenGL ES 2.0.  When this extension is available, these
     * built-in functions are also available, as is a hint controlling the
     * quality/performance trade off.
    </pre> *
     *
    </dd> *
     * <dt>Issues</dt>
     * <dd>
     *
     * <pre>
     * None.
    </pre> *
     *
    </dd> *
     * <dt>New Procedures and Functions</dt>
     * <dd>
     *
     * <pre>
     * None
    </pre> *
     *
    </dd> *
     * <dt>New Tokens</dt>
     * <dd>
     *
     * <pre>
     * Accepted by the <target> parameter of Hint and by the <pname> parameter of
     * GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev:
     *
     * FRAGMENT_SHADER_DERIVATIVE_HINT_OES            0x8B8B
    </pname></target></pre> *
     *
    </dd> *
     * <dt>New Built-in Functions</dt>
     * <dd>
     *
     * <pre>
     * dFdx()
     * dFdy()
     * fwidth()
    </pre> *
     *
    </dd> *
     * <dt>New Macro Definitions</dt>
     * <dd>
     *
     * <pre>
     * #define GL_OES_standard_derivatives 1
    </pre> *
     *
    </dd> *
     * <dt>Additions to Chapter 5 of the OpenGL ES 2.0 specification:</dt>
     * <dd>
     *
     * <pre>
     * In section 5.2 (Hints), add the following to the list of supported hints:
     *
     * FRAGMENT_SHADER_DERIVATIVE_HINT_OES
     *
     * Derivative accuracy for fragment processing built-in functions dFdx, dFdy
     * and fwidth.
    </pre> *
     *
    </dd> *
     * <dt>Additions to Chapter 8 of the OpenGL ES Shading Language specification:</dt>
     * <dd>
     *
     * <pre>
     * Replace section 8.8 (Fragment Processing Functions) with the following
     * paragraphs:
     *
     * Fragment processing functions are only available in fragment shaders.
     *
     * The built-in derivative functions dFdx, dFdy, and fwidth are optional, and
     * must be enabled by
     *
     * #extension GL_OES_standard_derivatives : enable
     *
     * before being used.
     *
     * Derivatives may be computationally expensive and/or numerically unstable.
     * Therefore, an OpenGL ES implementation may approximate the true derivatives
     * by using a fast but not entirely accurate derivative computation.
     *
     * The expected behavior of a derivative is specified using forward/backward
     * differencing.
     *
     * Forward differencing:
     *
     * F(startX+dx) - F(startX)   is approximately equal to    dFdx(startX).dx                  1a
     *
     * dFdx(startX)          is approximately equal to    F(startX+dx) - F(startX)              1b
     * --------------
     * dx
     *
     * Backward differencing:
     *
     * F(startX-dx) - F(startX)   is approximately equal to    -dFdx(startX).dx                 2a
     *
     * dFdx(startX)          is approximately equal to    F(startX) - F(startX-dx)              2b
     * --------------
     * dx
     *
     *
     * With single-sample rasterization, dx <= 1.0 in equations 1b and 2b.  For
     * multi-sample rasterization, dx < 2.0 in equations 1b and 2b.
     *
     * dFdy is approximated similarly, with startY replacing startX.
     *
     * An OpenGL ES implementation may use the above or other methods to perform
     * the calculation, subject to the following conditions:
     *
     * 1. The method may use piecewise linear approximations.  Such linear
     * approximations imply that higher order derivatives, dFdx(dFdx(startX)) and
     * above, are undefined.
     *
     * 2. The method may assume that the function evaluated is continuous.
     * Therefore derivatives within the body of a non-uniform conditional are
     * undefined.
     *
     * 3. The method may differ per fragment, subject to the constraint that the
     * method may vary by window coordinates, not screen coordinates.  The
     * invariance requirement described in section 3.1 of the OpenGL ES 2.0
     * specification is relaxed for derivative calculations, because the method
     * may be a function of fragment location.
     *
     * Other properties that are desirable, but not required, are:
     *
     * 4. Functions should be evaluated within the interior of a primitive
     * (interpolated, not extrapolated).
     *
     * 5. Functions for dFdx should be evaluated while holding startY constant.
     * Functions for dFdy should be evaluated while holding startX constant.
     * However, mixed higher order derivatives, like dFdx(dFdy(startY)) and
     * dFdy(dFdx(startX)) are undefined.
     *
     * 6. Derivatives of constant arguments should be 0.
     *
     * In some implementations, varying degrees of derivative accuracy may be
     * obtained by providing GL hints (section 5.6 of the OpenGL ES 2.0
     * specification), allowing a user to make an image quality versus speed trade
     * off.
    </pre> *
     *
    </dd> *
     * <dt>GLSL ES functions</dt>
     * <dd>
     *
     * <pre>
     * genType dFdx (genType p)
     *
     * Returns the derivative in startX using local differencing for the input argument
     * p.
     *
     * genType dFdy (genType p)
     *
     * Returns the derivative in startY using local differencing for the input argument
     * p.
     *
     * These two functions are commonly used to estimate the filter width used to
     * anti-alias procedural textures.  We are assuming that the expression is
     * being evaluated in parallel on a SIMD array so that at any given point in
     * time the value of the function is known at the grid points represented by
     * the SIMD array.  Local differencing between SIMD array elements can
     * therefore be used to derive dFdx, dFdy, etc.
     *
     * genType fwidth (genType p)
     *
     * Returns the sum of the absolute derivative in startX and startY using local
     * differencing for the input argument p, i.e.:
     *
     * abs (dFdx (p)) + abs (dFdy (p));
    </pre> *
     *
    </dd> *
     * <dt>New State</dt>
     * <dd>
     *
     * <pre>
     * Add to Table 6.27: Hints
     *
     * Get KeyValue                    Type  Get Command  Initial KeyValue  Description
     * ---------                    ----  -----------  -------------  -----------
     * FRAGMENT_SHADER_DERIVATIVE_  Z3    GetIntegerv  DONT_CARE      Fragment shader
     * HINT_OES                                                       derivative
     * accuracy hint
    </pre> *
     *
    </dd> *
     * <dt>Revision History</dt>
     * <dd>
     *
     * <pre>
     * 7/07/2005  Created.
     * 7/06/2006  Removed from main specification document.
     * 7/18/2007  Updated to match desktop GLSL spec and added hint.
    </pre> *
     *
    </dd> *
     *
    </dl> *
     *
     */
    STANDARD_DERIVATES("GL_OES_standard_derivatives", false),

    /**
     *
     *
     * Preso da [OES_EGL_image_external](http://www.khronos.org/registry/gles/extensions/OES/OES_EGL_image_external.txt).
     *
     *
     * <dl>
     *
     * <dt>Name</dt>
     * <dd>
     *
     * <pre>
     * OES_EGL_image_external
    </pre> *
     *
    </dd> *
     *
     *
     * <dt>Status</dt>
     * <dd>
     *
     * <pre>
     * Ratified by Khronos Promoters, 10 December 2010
     * Approved by the OpenGL ES Working Group, 15 September 2010
    </pre> *
     *
    </dd> *
     * <dt>Version</dt>
     * <dd>
     *
     * <pre>
     * July 19, 2012 (version 23)
    </pre> *
     *
    </dd> *
     * <dt>Number</dt>
     * <dd>
     *
     * <pre>
     * OpenGL ES Extension #87
    </pre> *
     *
    </dd> *
     * <dt>Dependencies</dt>
     * <dd>
     *
     * <pre>
     * Requires OpenGL ES 1.1 or OpenGL ES 2.0.
     *
     * Requires EGL 1.2
     *
     * Requires either the EGL_KHR_image_base or the EGL_KHR_image extension
     *
     * This extension is written based on the wording of the OpenGL ES 2.0
     * specification, the OpenGL ES Shading Language version 1.0, and the
     * EGL_KHR_image_base extension.  On an OpenGL ES 2.0 implementation, ignore
     * sections which are added only to the OpenGL ES 1.1 specification.
     *
     * This extension is also written based on the wording of the OpenGL ES 1.1
     * specification.  On an OpenGL ES 1.X implementation, ignore sections which
     * are added only to the OpenGL ES 2.0 or OpenGL ES Shading Language
     * specification.  Also ignore all mention of MAX_VERTEX_TEXTURE_IMAGE_UNITS,
     * and MAX_COMBINED_TEXTURE_IMAGE_UNITS.
     *
     * This extension borrows concepts and function names from the
     * GL_OES_EGL_image extension.  This extesion is compatible with, but does
     * not require, the GL_OES_EGL_image extension.
    </pre> *
     *
    </dd> *
     * <dt>Overview</dt>
     * <dd>
     *
     * <pre>
     * This extension provides a mechanism for creating EGLImage texture targets
     * from EGLImages.  This extension defines a new texture target,
     * TEXTURE_EXTERNAL_OES.  This texture target can only be specified using an
     * EGLImage.  There is no support for most of the functions that manipulate
     * other texture targets (e.g. you cannot use gl*Tex*Image*() functions with
     * TEXTURE_EXTERNAL_OES).  Also, TEXTURE_EXTERNAL_OES targets never have more
     * than a single LOD level.  Because of these restrictions, it is possible to
     * bind EGLImages which have internal formats not otherwise supported by
     * OpenGL ES.  For example some implementations may allow EGLImages with
     * planar or interleaved YUV data to be GLES texture target siblings.  It is
     * up to the implementation exactly what formats are accepted.
    </pre> *
     *
    </dd> *
     * <dt>Glossary</dt>
     * <dd>
     *
     * <pre>
     * Please see the EGL_OES_image_base specification for a list of terms
     * used by this specification.
    </pre> *
     *
    </dd> *
     * <dt>New Types</dt>
     * <dd>
     *
     * <pre>
     * // GLeglImageOES is an opaque handle to an EGLImage
     * // Note: GLeglImageOES is also defined in GL_OES_EGL_image
     * typedef void* GLeglImageOES;
     *
     * // (OpenGL ES 2.x only)
     * // New sampler allocation allowed by the OpenGL ES Shading Language when
     * //      #extension GL_OES_EGL_image_external
     * // is used.
     * samplerExternalOES
    </pre> *
     *
    </dd> *
     * <dt>New Procedures and Functions</dt>
     * <dd>
     *
     * <pre>
     * // Note: EGLImageTargetTexture2DOES is also defined in GL_OES_EGL_image
     * void EGLImageTargetTexture2DOES(enum target, eglImageOES image)
    </pre> *
     *
    </dd> *
     * <dt>New Tokens</dt>
     * <dd>
     *
     * <pre>
     * Accepted as a target in the <target> parameter of BindTexture and
     * EGLImageTargetTexture2DOES:
     *
     * TEXTURE_EXTERNAL_OES                                0x8D65
     *
     * (OpenGL ES 2.x only) Returned in the <allocation> parameter of GetActiveUniform:
     * SAMPLER_EXTERNAL_OES                                0x8D66
     *
     * Accepted as <value> in GetIntegerv() and GetFloatv() queries:
     * TEXTURE_BINDING_EXTERNAL_OES                        0x8D67
     *
     * Accepted as <value> in GetTexParameter*() queries:
     * REQUIRED_TEXTURE_IMAGE_UNITS_OES                    0x8D68
     *
    </value></value></allocation></target></pre> *
     *
    </dd> *
     * <dt>Additions to Chapter 3 of the OpenGL 1.1 or OpenGL 2.0 Specification</dt>
     * <dd>
     *
     * <pre>
     * - For OpenGL ES 2.0 add a new section: "3.7.14 External Textures," which
     * follows section "3.7.13 Texture Objects."  For OpenGL ES 1.1 add a new
     * section after section "3.7.11 Texture Objects."
     *
     * "3.7.14 External Textures
     *
     * External textures cannot be used with TexImage2D, TexSubImage2D,
     * CompressedTexImage2D, CompressedTexSubImage2D, CopyTexImage2D, or
     * CopyTexSubImage2D, and an INVALID_ENUM error will be generated if this
     * is attempted.  Their default min filter is LINEAR.  It is an
     * INVALID_ENUM error to set the min filter value to anything other than
     * LINEAR or NEAREST.  The default s and t wrap modes are CLAMP_TO_EDGE
     * and it is an INVALID_ENUM error to set the wrap mode to any other
     * value.  (For OpenGL ES 1.x only: The texture parameter GENERATE_MIPMAP
     * is always FALSE, and setting it to any other value generates an
     * INVALID_ENUM error.  (For OpenGL ES 2.x only: Calling GenerateMipmaps
     * with <target> set to TEXTURE_EXTERNAL_OES results in an INVALID_ENUM.)
     *
     * The command
     *
     * void EGLImageTargetTexture2DOES(enum target, eglImageOES image);
     *
     * with <target> set to TEXTURE_EXTERNAL_OES defines the currently bound
     * external texture object to be a target sibling of [image].  The width,
     * height, format, allocation, internalformat, border, and image data are all
     * determined based on the specified eglImageOES [image].  Any sibling
     * previously associated with this external texture object is deleted.
     *
     * A EGLImageTargetTexture2DOES() call with <target> set to
     * TEXTURE_EXTERNAL_OES will not modify the pixel data values in the
     * EGLImage.
     *
     * [image] must be the handle of a valid EGLImage resource, cast into the
     * allocation eglImageOES.
     *
     * Assuming no errors are generated in EGLImageTargetTexture2DOES, the
     * newly specified texture object will be an EGLImage target of the
     * specified eglImageOES.
     *
     * If the GL is unable to specify a texture object using the supplied
     * eglImageOES [image] (if, for example, [image] refers to a multisampled
     * eglImageOES), the error INVALID_OPERATION is generated.
     *
     * If <target> is not TEXTURE_EXTERNAL_OES, the error INVALID_ENUM is
     * generated.  (Note: if GL_OES_EGL_image is supported then <target> may
     * also be TEXTURE_2D).
     *
     * Sampling an external texture which is not associated with any EGLImage
     * sibling will return a sample value of (0,0,0,1).
     *
     * Each TEXTURE_EXTERNAL_OES texture object may require up to 3 texture
     * image units for each texture unit to which it is bound.  The number of
     * texture image units required by a bound texture object can be queried
     * using GetTexParameteriv() with <target> set to the texture target in
     * question, <value> set to REQUIRED_TEXTURE_IMAGE_UNITS_OES, and
     * ActiveTexture set to the texture unit to which the texture object is
     * bound.  When <target> is set to TEXTURE_EXTERNAL_OES this value will
     * be between 1 and 3 (inclusive).  For other valid texture targets this
     * value will always be 1.  Note that, when a TEXTURE_EXTERNAL_OES
     * texture object is bound, the number of texture image units required by
     * a single texture unit may be 1, 2, or 3, while for other texture
     * objects each texture unit requires exactly 1 texture image unit.
     *
     * (For OpenGL ES 1.x only) DrawArrays and DrawElements will fail and
     * generate an INVALID_OPERATION error if the number of texture image
     * units required is greater than the number available.  The number of
     * required texture image units is equal to the sum of the requirements
     * for each enabled texture unit.  For each texture unit the requirement
     * is the value returned by GetTexParameteriv() when <value> is set to
     * REQUIRED_TEXTURE_IMAGE_UNITS_OES, <target> is set to the target which
     * is enabled, and ActiveTexture has been set to the texture unit in
     * question.
     *
     * (For OpenGL ES 2.x only) DrawArrays and DrawElements will fail and
     * generate an INVALID_OPERATION error if the number of texture image
     * units required is greater than the number available.  The number of
     * required texture image units for each stage (fragment or vertex) is
     * equal to the sum of the requirements for each sampler referenced by
     * the stage.  A sampler is considered referenced if its location
     * (returned by GetUniformLocation) is not -1.  For each referenced
     * sampler the requirement is the value returned by GetTexParameteriv()
     * when <value> is set to REQUIRED_TEXTURE_IMAGE_UNITS_OES, <target> is
     * set to the target corresponding to the sampler allocation, and ActiveTexture
     * has been set to the texture unit corresponding to the value of the
     * sampler uniform.  The condition can be detected by calling Validate.
     *
     * (For OpenGL ES 2.x only) A shader that uses external texture samplers
     * may require some number of hidden uniform variables to implement the
     * conversion from YUV to RGB, and this may reduce the amount of space
     * available for uniforms defined by the shader program.  This may cause
     * a shader program which was working to stop working when a different
     * external texture is used (i.e. when Uniform1i or BindTexture is
     * called).  If there are not enough uniforms available then calls to
     * DrawArrays or DrawElements will fail and generate an INVALID_OPERATION
     * error.  This condition can be detected by calling ValidateProgram.
     *
     * Sampling an external texture which has been modified since it was
     * bound will return samples which may correspond to image values either
     * before, during, or after the modification.  Binding (or re-binding if
     * already bound) an external texture by calling BindTexture after all
     * modifications are complete guarantees that sampling done in future
     * draw calls will return values corresponding to the values in the
     * buffer at or after the time that BindTexture is called.  (Note that
     * calling BindTexture after calling commands that modify the EGLImage
     * may not be sufficient to ensure that the modifications are complete;
     * additional synchronization (for example eglWaitAPI, eglWaitNative,
     * glFinish, vgFinish, or eglWaitSyncKHR, etc) may be required following
     * the call initiating modifications, to ensure they have taken effect on
     * the texture, before the call to BindTexture is made.)
     *
     * Sampling an external texture will return an RGBA vector in the same
     * colorspace as the source image.  If the source image is stored in YUV
     * (or some other basis) then the YUV values will be transformed to RGB
     * values. (But these RGB values will be in the same colorspace as the
     * original image.  Colorspace here includes the linear or non-linear
     * encoding of the samples. For example, if the original image is in the
     * sRGB color space then the RGB value returned by the sampler will also
     * be sRGB, and if the original image is stored in ITU-R Rec. 601 YV12
     * then the RGB value returned by the sampler will be an RGB value in the
     * ITU-R Rec. 601 colorspace.) The parameters of the transformation
     * from one basis (e.g.  YUV) to RGB (color conversion matrix, sampling
     * offsets, etc) are taken from the EGLImage which is associated with the
     * external texture.  The implementation may choose to do this
     * transformation when the external texture is sampled, when the external
     * texture is bound, or any other time so long as the effect is the same.
     * It is undefined whether texture filtering occurs before or after the
     * transformation to RGB.
     *
     * If the EGLImage associated with the external texture contains alpha
     * values then the value of the alpha component returned is taken from
     * the image; otherwise the alpha component is 1.0.
     *
     * Changes to section "2.10.4 Shader Variables" of the OpenGL ES 2.0
     * specification (ignore for OpenGL ES 1.x)
     *
     * - In the subsection "Uniform Variables" paragraph 13, last sentence, add
     *
     * "SAMPLER_EXTERNAL_OES"
     * to the list of types that can be returned.
     *
     * Changes to section "2.10.5 Shader Execution" of the OpenGL ES 2.0
     * specification (ignore for OpenGL ES 1.x)
     *
     * - In the subsection "Validation", add to the bullet points in the second
     * paragraph:
     *
     * "- the number of texture image units required is greater than
     * the number available (see REQUIRED_TEXTURE_IMAGE_UNITS_OES,
     * MAX_COMBINED_TEXTURE_IMAGE_UNITS, MAX_VERTEX_TEXTURE_IMAGE_UNITS,
     * and MAX_TEXTURE_IMAGE_UNITS).
     *
     * - the number of uniforms required to implement the shader program is
     * greater than the number available."
     *
     * Changes to section "3.7.5 Texture Parameters" of the OpenGL ES 1.1 specification
     *
     * - Add to the end of the section a new paragraph:
     *
     * "When <target> is TEXTURE_EXTERNAL_OES only NEAREST and LINEAR are
     * accepted as TEXTURE_MIN_FILTER, only CLAMP_TO_EDGE is accepted as
     * TEXTURE_WRAP_S and TEXTURE_WRAP_T, and only FALSE is accepted as
     * GENERATE_MIPMAP.  Attempting to set other values for
     * TEXTURE_MIN_FILTER, TEXTURE_WRAP_S, TEXTURE_WRAP_T, or GENERATE_MIPMAP
     * will result in an INVALID_ENUM error.
     *
     * Changes to section "3.7.4 Texture Parameters" of the OpenGL ES 2.0 Specification
     *
     * - Add to the end of the section a new paragraph:
     *
     * "When <target> is TEXTURE_EXTERNAL_OES only NEAREST and LINEAR are
     * accepted as TEXTURE_MIN_FILTER and only CLAMP_TO_EDGE is accepted as
     * TEXTURE_WRAP_S and TEXTURE_WRAP_T.  Attempting to set other values for
     * TEXTURE_MIN_FILTER, TEXTURE_WRAP_S, or TEXTURE_WRAP_T will result in
     * an INVALID_ENUM error.
     *
     * Changes to section "3.7.10 Texture State" of the OpenGL ES 1.1 Specification
     * and section "3.7.12 Texture State" of the OpenGL ES 2.0 Specification
     *
     * - Add an additional sentence at the end of the section:
     *
     * "In the initial state of a TEXTURE_EXTERNAL_OES texture object,
     * the value assigned to TEXTURE_MIN_FILTER and TEXTURE_MAG_FILTER is
     * LINEAR, and the s and t wrap modes are both set to
     * CLAMP_TO_EDGE."
     *
     * Changes to section "3.7.11 Mipmap Generation" of the OpenGL ES 2.0 Specification
     * (ignore for OpenGL ES 1.x)
     *
     * - Add two additional paragraphs to the end of the section:
     *
     * "When <target> is set to TEXTURE_EXTERNAL_OES, GenerateMipmap
     * always fails and generates an INVALID_ENUM error."
     *
     * Changes to section "3.7.11 Texture Objects" of the OpenGL ES 1.1 specification
     * (ignore for OpenGL ES 2.x)
     *
     * - Modify this section as follows: (changed lines marked with *, and added
     * lines are marked with +)
     *
     * "In addition to the default textures TEXTURE_2D and
     * TEXTURE_EXTERNAL_OES, named two-dimensional and external texture
     * objects can be created and operated upon.  The name space for texture
     * objects is the unsigned integers, with zero reserved by the GL.
     *
     * A texture object is created by binding an unused name to
     * TEXTURE_2D or TEXTURE_EXTERNAL_OES. The binding is effected by calling
     * void BindTexture( enum target, uint texture );
     * with target set to the desired texture target and texture set to the
     * unused name. The resulting texture object is a new state vector,
     * comprising all the state values listed in section 3.7.10, set to the
     * +       same initial values. If the new texture object is bound to TEXTURE_2D
     * +       or TEXTURE_EXTERNAL_OES it is and remains a two-dimensional or
     * +       external texture respectively until it is deleted.
     *
     * BindTexture may also be used to bind an existing texture object to
     * TEXTURE_2D or TEXTURE_EXTERNAL_OES. If the bind is successful no
     * change is made to the state of the bound texture object, and any
     * previous binding to target is broken.
     *
     * While a texture object is bound, GL operations on the target to
     * which it is bound affect the bound object, and queries of the target
     * to which it is bound return state from the bound object. If texture
     * mapping is enabled, the state of the bound texture object directs the
     * texturing operation.
     *
     * In the initial state, TEXTURE_2D and TEXTURE_EXTERNAL_OES have
     * two-dimensional and external texture state vectors respectively
     * associated with them.  In order that access to these initial textures
     * not be lost, they are treated as texture objects all of whose names
     * are 0. The initial two-dimensional and external texture are therefore
     * operated upon, queried, and applied as TEXTURE_2D or
     * TEXTURE_EXTERNAL_OES respectively while 0 is bound to the
     * corresponding targets.
     *
     * Texture objects are deleted by calling
     * void DeleteTextures( sizei n, uint *textures );
     * textures contains n names of texture objects to be deleted. After a
     * texture object is deleted, it has no contents, and its name is again
     * unused. If a texture that is currently bound to the target TEXTURE_2D
     * or TEXTURE_EXTERNAL_OES is deleted, it is as though BindTexture had
     * been executed with the same target and texture zero. Unused names in
     * textures are silently ignored, as is the value zero.
     *
     * The command
     * void GenTextures( sizei n, uint *textures );
     * returns n previously unused texture object names in textures. These
     * names are marked as used, for the purposes of GenTextures only, but
     * they acquire texture state only when they are first bound, just as if
     * they were unused.
     *
     * The texture object name space, including the initial texture
     * object, is shared among all texture units. A texture object may be
     * bound to more than one texture unit simultaneously. After a texture
     * object is bound, any GL operations on that target object affect any
     * other texture units to which the same texture object is bound.
     *
     * Texture binding is affected by the setting of the state
     * ACTIVE_TEXTURE.
     *
     * If a texture object is deleted, it is as if all texture units which
     * are bound to that texture object are rebound to texture object zero.
     *
     * Changes to section "3.7.13 Texture Objects" of the OpenGL ES 2.0 specification
     * (ignore for OpenGL ES 1.x)
     *
     * - Modify this section as follows: (changed lines marked with *, and added
     * lines are marked with +)
     *
     * "In addition to the default textures TEXTURE_2D, TEXTURE_CUBE_MAP,
     * and TEXTURE_EXTERNAL_OES, named two-dimensional, cube map, and
     * external texture objects can be created and operated upon.  The name
     * space for texture objects is the unsigned integers, with zero reserved
     * by the GL.
     *
     * A texture object is created by binding an unused name to
     * TEXTURE_2D, TEXTURE_CUBE_MAP, or TEXTURE_EXTERNAL_OES. The binding is
     * effected by calling
     * void BindTexture( enum target, uint texture );
     * with target set to the desired texture target and texture set to the
     * unused name. The resulting texture object is a new state vector,
     * comprising all the state values listed in section 3.7.12, set to the
     * same initial values. If the new texture object is bound to TEXTURE_2D,
     * TEXTURE_CUBE_MAP, or TEXTURE_EXTERNAL_OES it is and remains a
     * two-dimensional, cube map, or external texture respectively until it
     * is deleted.
     *
     * BindTexture may also be used to bind an existing texture object to
     * either TEXTURE_2D, TEXTURE_CUBE_MAP, or TEXTURE_EXTERNAL_OES. The error
     * INVALID_OPERATION is generated if an attempt is made to bind a texture
     * object of different target than the specified target. If the bind is
     * successful no change is made to the state of the bound texture object,
     * and any previous binding to target is broken.
     *
     * While a texture object is bound, GL operations on the target to
     * which it is bound affect the bound object, and queries of the target to
     * which it is bound return state from the bound object. If texture
     * mapping is enabled, the state of the bound texture object directs the
     * texturing operation.
     *
     * In the initial state, TEXTURE_2D, TEXTURE_CUBE_MAP, and
     * TEXTURE_EXTERNAL_OES have two-dimensional, cube map, and external
     * texture state vectors respectively associated with them.  In order
     * that access to these initial textures not be lost, they are treated as
     * texture objects all of whose names are 0. The initial two-dimensional,
     * cube map, and external texture are therefore operated upon, queried,
     * and applied as TEXTURE_2D, TEXTURE_CUBE_MAP, or TEXTURE_EXTERNAL_OES
     * respectively while 0 is bound to the corresponding targets.
     *
     * Texture objects are deleted by calling
     * void DeleteTextures( sizei n, uint *textures );
     * textures contains n names of texture objects to be deleted. After a
     * texture object is deleted, it has no contents or dimensionality, and
     * its name is again unused. If a texture that is currently bound to one
     * of the targets TEXTURE_2D, TEXTURE_CUBE_MAP, or TEXTURE_EXTERNAL_OES
     * is deleted, it is as though BindTexture had been executed with the
     * same target and texture zero.  Unused names in textures are silently
     * ignored, as is the value zero.
     *
     * The command
     * void GenTextures( sizei n, uint *textures );
     * returns n previously unused texture object names in textures. These
     * names are marked as used, for the purposes of GenTextures only, but
     * they acquire texture state only when they are first bound, just as if
     * they were unused.
     *
     * The texture object name space, including the initial texture
     * object, is shared among all texture units. A texture object may be
     * bound to more than one texture unit simultaneously. After a texture
     * object is bound, any GL operations on that target object affect any
     * other texture units to which the same texture object is bound.
     *
     * Texture binding is affected by the setting of the state
     * ACTIVE_TEXTURE.
     *
     * If a texture object is deleted, it is as if all texture units which
     * are bound to that texture object are rebound to texture object zero.
     *
     * Changes to section "3.7.13 Texture Application" of the OpenGL ES 1.1
     * specification (ignore for OpenGL ES 2.x)
     *
     * - Replace the first sentence of the first paragraph with:
     *
     * "Texturing is enabled or disabled using the generic Enable and
     * Disable commands, with the symbolic constant TEXTURE_2D or
     * TEXTURE_EXTERNAL_OES to enable or disable texturing with the
     * TEXTURE_2D or TEXTURE_EXTERNAL_OES texture target, respectively.  If
     * TEXTURE_EXTERNAL_OES is enabled it takes precedence over TEXTURE_2D,
     * TEXTURE_CUBE_MAP_OES, TEXTURE_RECTANGLE_ARB, and
     * TEXTURE_RECTANGLE_NV."
     *
     * Changes to section "3.8.2 Shader Execution" of the OpenGL ES 2.0
     * specification (ignore for OpenGL ES 1.x)
     *
     * - Add to the end of the last paragraph of the subsection "Texture Access"
     *
     * "The REQUIRED_TEXTURE_IMAGE_UNITS_OES parameter can change based on the
     * number of external textures which are currently bound (see section
     * 3.7.14)."
     *
     * Changes to section "6.1.3 Enumerated Queries" of the OpenGL ES 1.1 specification
     *
     * - Change:
     * "...TEXTURE_2D..."
     * to
     * "...TEXTURE_2D or TEXTURE_EXTERNAL..."
     * in the last paragraph.
     *
     * Changes to section "6.1.3 Enumerated Queries" of the OpenGL ES 2.0 specification
     *
     * - Change:
     * "...TEXTURE_2D or TEXTURE_CUBE_MAP..."
     * to
     * "...TEXTURE_2D, TEXTURE_CUBE_MAP, or TEXTURE_EXTERNAL..."
     * in the second paragraph.
     *
     *
     * Changes to section "6.2 State Tables" of the OpenGL ES 1.1 specification
     *
     * - Add to "Table 6.13 Textures (state per texture unit and binding point)"
     *
     * +-------------------+-------+-------------+-------+------------------+
     * | TEXTURE_BINDING_- | 8*xZ+ | GetIntegerv | 0     | Texture object   |
     * |   EXTERNAL_OES    |       |             |       | bound to         |
     * |                   |       |             |       | TEXTURE_-        |
     * |                   |       |             |       |    EXTERNAL_OES  |
     * +-------------------+-------+-------------+-------+------------------+
     * | TEXTURE_-         | 2*xB  | IsEnabled   | False | True if external |
     * |   EXTERNAL_OES    |       |             |       | texturing is     |
     * |                   |       |             |       | enabled          |
     * +-------------------+-------+-------------+---+----------------------+
     *
     * - Add to "Table 6.14 Textures (state per texture object)"
     *
     * +--------------------+-------+-----------------+---+----------------+
     * | REQUIRED_TEXTURE_- | n*xZ3 | GetTexParameter | 1 | Number of      |
     * |   IMAGE_UNITS_OES  |       |                 |   | texture image  |
     * |                    |       |                 |   | units required |
     * |                    |       |                 |   |    by texture  |
     * +--------------------+-------+-----------------+---+----------------+
     *
     * Changes to section "6.2 State Tables" of the OpenGL ES 2.0 specification
     *
     * - Add to "Table 6.7 Textures (state per texture unit and binding point)"
     *
     * +-------------------+-------+-------------+-------+------------------+
     * | TEXTURE_BINDING_- | 8*xZ+ | GetIntegerv | 0     | Texture object   |
     * |   EXTERNAL_OES    |       |             |       | bound to         |
     * |                   |       |             |       | TEXTURE_-        |
     * |                   |       |             |       |    EXTERNAL_OES  |
     * +-------------------+-------+-------------+-------+------------------+
     *
     * - Add to "Table 6.8 Textures (state per texture object)"
     *
     * +--------------------+-------+-----------------+---+----------------+
     * | REQUIRED_TEXTURE_- | n*xZ3 | GetTexParameter | 1 | Number of      |
     * |   IMAGE_UNITS_OES  |       |                 |   | texture image  |
     * |                    |       |                 |   | units required |
     * |                    |       |                 |   |    by texture  |
     * +--------------------+-------+-----------------+---+----------------+
     *
     * Changes to section 3.7 of the OpenGL ES Shading Language specification
     *
     * - Add to the list of keywords:
     *
     * "samplerExternalOES"
     *
     * Changes to section 4.1 of the OpenGL ES Shading Language specification
     *
     * - Add to the list of basic types:
     *
     * "samplerExternalOES   a handle for accessing an external texture"
     *
     * Changes to section 4.5.3 of the OpenGL ES Shading Language specification
     *
     * - Add to the list of "vertex language has the following predeclared
     * globally scoped default precision statements:"
     *
     * "precision lowp samplerExternalOES;"
     *
     * - Add to the list of "fragment language has the following predeclared
     * globally scoped default precision statements:"
     *
     * "precision lowp samplerExternalOES;"
     *
     * Changes to section 8.7 of the OpenGL ES Shading Language specification
     *
     * - Add to the table the following sampler functions:
     *
     * "vec4 texture2D(
     * samplerExternalOES sampler,
     * vec2 coord)
     * vec4 texture2DProj(
     * samplerExternalOES sampler,
     * vec3 coord)
     * vec4 texture2DProj(
     * samplerExternalOES sampler,
     * vec4 coord)"
     *
     *
     * Changes to section 9 of the OpenGL ES Shading Language specification
     *
     * - Add to token list:
     *
     * "SAMPLEREXTERNAL"
     *
     * - Add following "type_specifier_no_prec:"
     *
     * "SAMPLEREXTERNAL"
     *
     * Changes to section "A.7 Counting of Varyings and Uniforms" of the OpenGL ES
     * Shading Language specification
     *
     * - Change the first sentence of the last paragraph to
     *
     * "Part of the storage may be reserved by an implementation for its own
     * use e.g. for computation of transcendental functions or conversion of
     * external textures."
    </target></target></target></target></value></target></value></target></value></target></target></target></target></target></target></pre> *
     *
    </dd> *
     * <dt>Issues</dt>
     * <dd>
     *
     * <pre>
     * 1.  What happens when GenerateMipmaps() is called with <target> set to
     * TEXTURE_EXTERNAL_OES?
     *
     * Possible options:
     *
     * A) Fail (INVALID_ENUM)
     * B) Generate the mipmaps and copy them into the TEXTURE_2D target.
     *
     * Option B could be useful and should not be difficult to implement.
     * What happens when the texture is NPOT and GenerateMipmaps() is called?
     *
     * Ben Bowman and David Garcia have indicated they prefer A.  No other
     * strong opinions yet.
     *
     * RESOLVED: A (fail)
     *
     * 2.  Should the wrap mode of an external texture be allowed to be anything
     * other than CLAMP_TO_EDGE?
     *
     * RESOLVED: no
     *
     * 3.  What about portability problems introduced by allowing implementation-
     * dependent failures?
     *
     * This is the same issue described in Issue 14 of the
     * EGL_OES_image specification.  Like the resolution for that issue,
     * this specification should include some minimum requirements, but
     * leave the larger portability problem unresolved at the moment.
     *
     * RESOLVED: This and other (EGL_KHR_image_uses and EGL_KHR_il_image)
     * extensions attempt to minimize this problem by allowing EGL to pick
     * EGLImage attributes which will work in various situations.  However it
     * is not possible to guarantee that every usecase will work on every
     * platform.
     *
     * 4.  Should EGLImageTargetTexture2DOES result in undefined pixel data,
     * as with calls to eglCreateImageOES?
     *
     * See also issue 4 of GL_OES_EGL_image
     *
     * Comment from Aske:
     * In the description for EGLImageTargetTexture2DOES, it says: "As a
     * result of this operation all pixel data in [image] will become
     * undefined." Why is this? I would imagine one would want to take an
     * existing EGL image containing image data and bind it as an external
     * texture, possibly multiple times, to use it for rendering. I have read
     * through issue 4 of OES_EGL_image, and it seems the concern here is
     * what happens when the image is (potentially) modified from OpenGL ES.
     * Since external images cannot be modified from OpenGL ES, I would guess
     * the same issues do not appply here.
     *
     * Comment from Acorn:
     * Another potentential issue is that the memory for the EGLImage may
     * have to be reallocated in order to be compatible with GLES (e.g. GLES
     * may have more strict alignment or other requirements).  However, the
     * implementation can fail (or do a copy) if this is the case.  If the
     * EGL_KHR_image_uses extension is used to specify that the EGLImage will
     * be used as a GLES external texture then this should not be an issue
     * because EGL will be able to allocate the memory correctly in the first
     * place.
     *
     * RESOLVED: pixel data will not become undefined when
     * EGLImageTargetTexture2DOES() is used with an external texture target.
     *
     * 5.  What happens if the texture is bound while the associated EGLImage is
     * being modified.
     *
     * Some implementations may need to color convert the data
     * after it has been generated.  A solution to this is to state that the
     * texels become undefined if the external texture is bound while the
     * underlying texels are modified.  This way any color conversion can be
     * done (e.g. to a secoond buffer) when the texture is bound.
     *
     * Options:
     * A) require the texture be unbound while the EGLImage is being modified
     * B) Require the texture to be bound (or re-bound if already bound)
     * between modification and use (i.e. after modification and before
     * used as a texture).
     * C) No requirement.
     *
     * RESOLVED: B
     *
     * 6.  How can current generation hardware support planar YUV EGLImage formats?
     *
     * GLES 2.0 hardware can perform color conversion by inserting
     * extra instructions into the shader program.  However, planar textures
     * require 2 or 3 surfaces to be read in order to sample a single texture.
     * One solution is to increase the number of texture image units required
     * by a texture object which is associated with such an EGLImage.
     *
     * RESOLVED: allow the implementation to set the number of texture
     * image units required by each texture object.  Allow the app to query
     * this with REQUIRED_TEXTURE_IMAGE_UNITS_OES.  Have drawing commands
     * fail (INVALID_OPERATION) when the number of texture image units
     * required by all enabled/referenced texture units exceeds the
     * implementation limit (which can be queried with
     * MAX_COMBINED_TEXTURE_IMAGE_UNITS, MAX_VERTEX_TEXTURE_IMAGE_UNITS, and
     * MAX_TEXTURE_IMAGE_UNITS)
     *
     * See also issue 10.
     *
     * 7.  Should there be new texture sampling functions for samplerExternalOES
     * or is it OK to use the existing 2D sampler functions.
     *
     * RESOLVED: Use existing 2D sampler functions.
     *
     * 8.  If an EGLImage associated with an external texture does not contain an
     * alpha channel, should the alpha be 1 or undefined.
     *
     * From an app point of view, 1 probably makes more sense.
     * However, if the texture is in a RGBA format and there is garbage in
     * the A channel, it may be difficult for implementations to return 1.
     * An implementation may have to recompile the shader to force the alpha
     * channel to 1.
     *
     * RESOLVED: alpha is defined to be 1 in this case.
     *
     * 9.  How should the color conversion be described.
     *
     * Options:
     *
     * A) State that the color values are transformed to a linear colorspace
     * and represented as RGB values in that space.  This may be difficult
     * for implementations since it may require nonlinear transformations
     * (e.g. gamma decoding).  It is also problematic because usually many
     * more bits are required to represent a value in a linear colorspace
     * than in a gamma encoded colorspace.
     *
     * B) State that the color is transformed to RGB but in the same
     * colorspace as the source image (i.e. no gamma encode or decode).
     *
     * RESOLVED: Option B.
     *
     * 10. How does the implementation indicate which texture samplers and/or
     * texture units are not available when one or more external textures are
     * bound.
     *
     * See discussion on GLES email list around Feb 23 with
     * subject "RE: [OpenGL-ES] IL/ES interaction: YUV texture extension
     * proposal"
     *
     * Proposals:
     *
     * A) The MAX_COMBINED_TEXTURE_IMAGE_UNITS,
     * MAX_VERTEX_TEXTURE_IMAGE_UNITS, and MAX_TEXTURE_IMAGE_UNITS parameters
     * change to reflect the current state.
     *
     * B) New queries AVAILABLE_COMBINED_TEXTURE_IMAGE_UNITS,
     * AVAILABLE_VERTEX_TEXTURE_IMAGE_UNITS, and
     * AVAILABLE_TEXTURE_IMAGE_UNITS indicate curent state, while
     * MAX_COMBINED_TEXTURE_IMAGE_UNITS, MAX_VERTEX_TEXTURE_IMAGE_UNITS, and
     * MAX_TEXTURE_IMAGE_UNITS remain constant and indicate the state when no
     * external texures are bound.
     *
     * C) Query for number of required texture image units per texture
     * object.  Draw calls fail with INVALID_OPERATION if the current
     * requirements for texture image units exceeds the number available.
     *
     * RESOLVED: C
     *
     * 11. What happens when glUniform1i() sets a sampler to a value that is
     * larger than the number of available texture units?
     *
     * NOTE: This issue no longer affects this extension.  See issue 10 for
     * different issue related to texture *image* units.
     *
     * This is really a GLES2 spec issue.  See
     * khronos bug 3702
     * https://cvs.khronos.org/bugzilla/show_bug.cgi?id=3702
     * http://www.khronos.org/members/login/list_archives/arb-glsl/
     * 0705/msg00010.html
     * http://www.khronos.org/members/login/list_archives/opengl_es/
     * 0902/msg00148.html
     *
     * RESOLVED: The issue is not resolved, but it no longer applies
     * directly to this extension.  Further discussion should be in bug 3702.
     *
     * 12. What happens at draw time when the current state requires more texture
     * units than the implementation can support?
     *
     * RESOLVED:  The precedent, from (desktop) OpenGL, is to fail in
     * glValidate, and to fail in any draw call with an INVALID_OPERATION
     * error.  Behavior described in this extension will match that
     * precedent.
     *
     * 13. How can this be conformance tested?
     *
     * In the native code, add a function which takes as a
     * parameter an array of pixels.  The function returns an EGLImage which
     * contains the pixels in some unspecified format.  A test can call this
     * function, call glEGLImageTargetTexImage2DOES() to associate it with an
     * external texture, and render using this texture, and read back the
     * framebuffer to confirm that the result is the expected result.
     *
     * Note1: since the format is unknown and unknowable, possibly only the
     * high bit of each component (RGB) should be tested.  It may be
     * sufficient to pass a single pixel (or single bit for each of R, G, an
     * B) and then create the EGLImage with every pixel in the image set to
     * that same value.
     *
     * Note2: It might be a good idea to have an extra parameter, int index,
     * which allows the function to be implemented several different ways.
     * The test could be run several times, with index set to 0, 1, 2, ...
     * until the function returns EGL_NO_IMAGE.  This way the function could
     * generate EGLImages with various formats.  For example when index is 0
     * it could call OMX IL to generate a YUV planar EGLImage, when index is
     * 1 it could call OMX AL to generate a YUV interleaved EGLImage, and
     * when index is 2 it could call into VG to generate an RGB EGLImage.
     * The meaning of each index would be up to the implementation.  A simple
     * implementation might return an EGLImage when index=0 and return
     * EGL_NO_IMAGE otherwise.
     *
     * Additional suggestion from Jon Leech:
     * The suggested conformance test in issue 13 is intentionally so vague
     * on precision (1 bit/component) that the conversion language seems even
     * less meaningful. The RGB bits that come out of texture sampling would
     * need have almost no relationship to what went in. ISTM the supplier of
     * the EGLImage knows the precision of it, and that information could be
     * provided to the test to put on tighter constraints.
     *
     * RESOLVED: As described above.
     *
     * 14. Can the number of uniforms required by a shader change when switching
     * from one external texture to another, and therefore cause the shader
     * to fail?  For example, imagine a program does this:
     * glLinkProgram(prog); // successful link
     * sampler = glGetUniformLocation(prog, "myExtSampler");
     * glUniform1i(sampler, 1);
     * glActiveTexture(GL_TEXTURE1);
     * glBindTexture(GL_TEXTURE_EXTERNAL, myTex1_rgb);
     * glDrawArrays(); // draw successfully
     *
     * glBindTexture(GL_TEXTURE_EXTERNAL, myTex2_yuv);
     * glDrawArrays();  // Can this fail???
     * Is it acceptable for an implementation to fail the second
     * glDrawArrays() call because myTex2_yuv is a yuv image and requires
     * more uniforms to implement a color conversion matrix than myTex1_rgb
     * which is an rgb image and does not require any conversion matrix?
     *
     * Note that this situation will only occur on implementation which will
     * recompile the shader based on the color conversion required.
     *
     * Options:
     *
     * A) No, the implementation is not allowed to fail.  When the program is
     * linked the implementation must verify that there are enough uniforms
     * to implement the program for any flavor of external texture.  If the
     * implementation does not provide enough uniforms for the program to
     * work with any external texture that the implementation supports, then
     * the implementation must fail at link time.
     *
     * B) Yes, the implememtation may fail in glDrawArrays or glDrawElements
     * if the number of uniforms required by the program exceeds those
     * available.  This means that a call to glUniform1i() or glBindTexture()
     * can cause a shader program that used to work to stop working.  (Note
     * that this is possible anyway since the new external texture may use
     * more texture image units than the old external texture (independent of
     * the number of uniforms). But that can be detected by the application
     * by querying REQUIRED_TEXTURE_IMAGE_UNITS_OES)
     *
     * RESOLVED: choice B.
     *
     * 15. How should filtering of non-RGB formats be specified.
     *
     * Options:
     *
     * A) filtering occurs before transformation to RGB.
     * B) filtering occurs after transformation to RGB.
     * C) undefined (up to the implementation)
     *
     * RESOLVED: C
    </target></pre> *
     *
    </dd> *
     * <dt>Dependencies on EGL_OES_image_base and EGL 1.1</dt>
     * <dd>
     *
     * <pre>
     * If either EGL 1.1 or the EGL_OES_image extension is not supported, all
     * discussion of EGLImages should be ignored, and any calls to
     * EGLImageTargetTexImage2DOES should generate the error INVALID_OPERATION.
    </pre> *
     *
    </dd> *
     * <dt>Dependencies on GL_OES_EGL_image</dt>
     * <dd>
     *
     * <pre>
     * If GL_OES_EGL_image is supported then change the text in both extensions
     * to allow either TEXTURE_2D or TEXTURE_EXTERNAL_OES to be passed as the
     * <target> parameter to EGLImageTargetTexImage2DOES().  When <target> is
     * TEXTURE_2D, behavior of EGLImageTargetTexImage2DOES() is as described in
     * the GL_OES_EGL_image spec.  When <target> is TEXTURE_EXTERNAL_OES,
     * behavior of EGLImageTargetTexImage2DOES() is as described in this spec.
    </target></target></target></pre> *
     *
    </dd> *
     * <dt>Revision History</dt>
     * <dd>
     *
     * <pre>
     * #23 - (July 19, 2012) Acorn Pooley
     * - fix extension name from OES_EGL_image_external to
     * GL_OES_EGL_image_external
     * #22 - (Dec 17, 2010) Acorn Pooley
     * status to ratified.
     * #21 - (Sept. 29, 2010) Maurice Ribble
     * Update token numbers and updated status to approved.
     * #20 - (April 2, 2009) Acorn Pooley
     * Rename GL_OES_egl_image_external to GL_OES_EGL_image_external to be
     * consistant with GL_OES_EGL_image.
     * #19 - (March 31, 2009) Acorn Pooley
     * Minor fixes
     * #18 - (March 30, 2009) Acorn Pooley
     * Fix inconsistancy
     * #17 - (March 25, 2009) Acorn Pooley
     * Resolved issue 4 (and some others)
     * #16 - (March 16, 2009) Acorn Pooley
     * Augment issue 13
     * #15 - (March 13, 2009) Acorn Pooley
     * fix dependancy section
     * resolve issue 14 and fix text
     * #14 - (March 9, 2009) Acorn Pooley
     * Fix colorspace comments.  Issue 9 is resolved.
     * #13 - (March 6, 2009) Acorn Pooley
     * Mark issue 6, 10, and 11 resolved.
     * Add issue 15.
     * #12 - (March 4, 2009) Acorn Pooley
     * Fix wording of uniform stuff.
     * #11 - (March 4, 2009) Acorn Pooley
     * Add issues 13, 14.
     * Add comments about extra uniforms required by external texture
     * samplers.
     * Allow external textures to work with default object 0.
     * Correct colorspace language.
     * #10 - (March 3, 2009) Acorn Pooley
     * Remove stale reference to MAX_TEXTURE_IMAGE_UNITS.  Add fix to OpenGL
     * ES 2.0 section 2.10.5 (from bruce).  Add contributors. Clarify
     * "texture unit" vs "texture image unit".  Fix typos.
     * #9 - (February 26, 2009) Acorn Pooley
     * Clarify sync further.  Add issue 12.  Change "too many texture units"
     * behavior.
     * #8 - (February 26, 2009) Acorn Pooley
     * Clarify sync requirements (i.e. that EGLImage does not do sync)
     * #7 - (February 25, 2009) Acorn Pooley
     * Relax binding requirement - see issue 5
     * #6 - (February 25, 2009) Acorn Pooley
     * Add TEXTURE_BINDING_EXTERNAL_OES.  Add texture state notes.  Modify
     * colorspace conversion to be a linear function.
     * #5 - (February 24, 2009) Acorn Pooley
     * Disable GenerateMipmaps.  Add issues 10 & 11.
     * #4 - (February 23, 2009) Acorn Pooley
     * Modify the "Texture Parameters" section in ES 1 and 2 specs
     * #3 - (February 20, 2009) Acorn Pooley
     * Clarify the reduction in number of texture units.
     * #2 - (February 20, 2009) Acorn Pooley
     * Fix some errors.  replace textureExternal* sampler functions with
     * existing sampler2D* functions.  Add issue 7,8,9.  Fix spelling.
     * #1 - (February 9, 2009) Original draft
    </pre> *
     *
    </dd> *
     *
     *
     *
    </dl> */
    IMAGE_EXTERNAL("GL_OES_EGL_image_external", false),

    /**
     *
     *
     * Preso da [OES_texture_npot](https://www.khronos.org/registry/gles/extensions/OES/OES_texture_npot.txt)
     *
     *
     *
     *
     * This extension adds support for the REPEAT and MIRRORED_REPEAT texture wrap modes and the minification filters supported for non-power of two 2D textures, cubemaps and for 3D textures, if the OES_texture_3D extension is supported.
     *
     *
     *
     *
     * Section 3.8.2 of the OpenGL ES 2.0 specification describes rules for sampling from an incomplete texture. There were specific rules added for non-power of two textures i.e. if the texture wrap mode is not CLAMP_TO_EDGE or
     * minification filter is not NEAREST or LINEAR and the texture is a non-power-of-two texture, then sampling the texture will return (0, 0, 0, 1).
     *
     *
     *
     *
     * These rules are no longer applied by an implementation that supports this extension.
     *
     *
     */
    TEXTURE_NPOT("OES_texture_npot", false);


    /**
     * Verifica che l'estensione rientri tra quelle che gestiamo. Se non è così restituisce null
     *
     * @param input
     *
     * @return restituisce l'estesione sottoforma di type nel caso in cui sia supportata, se non è supportata viene restituito null.
     */
    fun parseAndFlag(input: String): XenonGLExtension? {
        for (item in values()) {
            if (item.string == input) {
                item.isPresent = true
                return item
            }
        }
        return null
    }

}