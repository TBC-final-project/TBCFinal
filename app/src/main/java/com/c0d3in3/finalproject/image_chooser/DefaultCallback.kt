package com.c0d3in3.finalproject.image_chooser

/**
 * Stas Parshin
 * 05 November 2015
 */
abstract class DefaultCallback : EasyImage.Callbacks {

    override fun onImagePickerError(error: Throwable, source: MediaSource) {}

    override fun onCanceled(source: MediaSource) {}
}
