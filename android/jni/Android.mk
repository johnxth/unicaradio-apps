mydir := $(call my-dir)

#
# Include the aac.decoders property:
#
include $(mydir)/../jni.properties

AAC_DECODERS 	:=	$(aac.decoders)
LOGLEVEL 		:=	$(jni.loglevel)

include $(mydir)/aac-decoder/Android.mk

dump:
	$(warning $(modules-dump-database))
	$(warning $(dump-src-file-tags))
	$(error Dump finished)
