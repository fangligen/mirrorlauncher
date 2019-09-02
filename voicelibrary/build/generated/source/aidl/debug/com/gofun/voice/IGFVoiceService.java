/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\gofun\\GoFun_Launcher\\voicelibrary\\src\\main\\aidl\\com\\gofun\\voice\\IGFVoiceService.aidl
 */
package com.gofun.voice;
// Declare any non-default types here with import statements

public interface IGFVoiceService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.gofun.voice.IGFVoiceService
{
private static final java.lang.String DESCRIPTOR = "com.gofun.voice.IGFVoiceService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.gofun.voice.IGFVoiceService interface,
 * generating a proxy if needed.
 */
public static com.gofun.voice.IGFVoiceService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.gofun.voice.IGFVoiceService))) {
return ((com.gofun.voice.IGFVoiceService)iin);
}
return new com.gofun.voice.IGFVoiceService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_doPlayText:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
com.gofun.voice.IGFVoicePlayListener _arg1;
_arg1 = com.gofun.voice.IGFVoicePlayListener.Stub.asInterface(data.readStrongBinder());
this.doPlayText(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_doWakeup:
{
data.enforceInterface(descriptor);
com.gofun.voice.IGFVoiceWakeupListener _arg0;
_arg0 = com.gofun.voice.IGFVoiceWakeupListener.Stub.asInterface(data.readStrongBinder());
this.doWakeup(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_doRecognition:
{
data.enforceInterface(descriptor);
com.gofun.voice.IGFVoiceSpeechListener _arg0;
_arg0 = com.gofun.voice.IGFVoiceSpeechListener.Stub.asInterface(data.readStrongBinder());
this.doRecognition(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isTTSPlaying:
{
data.enforceInterface(descriptor);
boolean _result = this.isTTSPlaying();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isInited:
{
data.enforceInterface(descriptor);
boolean _result = this.isInited();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_isRecognition:
{
data.enforceInterface(descriptor);
boolean _result = this.isRecognition();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopTTs:
{
data.enforceInterface(descriptor);
this.stopTTs();
reply.writeNoException();
return true;
}
case TRANSACTION_stopRecognition:
{
data.enforceInterface(descriptor);
this.stopRecognition();
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.gofun.voice.IGFVoiceService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void doPlayText(java.lang.String text, com.gofun.voice.IGFVoicePlayListener playListener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
_data.writeStrongBinder((((playListener!=null))?(playListener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_doPlayText, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void doWakeup(com.gofun.voice.IGFVoiceWakeupListener wakeupListener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((wakeupListener!=null))?(wakeupListener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_doWakeup, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void doRecognition(com.gofun.voice.IGFVoiceSpeechListener speechListener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((speechListener!=null))?(speechListener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_doRecognition, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isTTSPlaying() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isTTSPlaying, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isInited() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isInited, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isRecognition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isRecognition, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void stopTTs() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopTTs, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopRecognition() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopRecognition, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_doPlayText = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_doWakeup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_doRecognition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_isTTSPlaying = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isInited = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_isRecognition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_stopTTs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_stopRecognition = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
}
public void doPlayText(java.lang.String text, com.gofun.voice.IGFVoicePlayListener playListener) throws android.os.RemoteException;
public void doWakeup(com.gofun.voice.IGFVoiceWakeupListener wakeupListener) throws android.os.RemoteException;
public void doRecognition(com.gofun.voice.IGFVoiceSpeechListener speechListener) throws android.os.RemoteException;
public boolean isTTSPlaying() throws android.os.RemoteException;
public boolean isInited() throws android.os.RemoteException;
public boolean isRecognition() throws android.os.RemoteException;
public void stopTTs() throws android.os.RemoteException;
public void stopRecognition() throws android.os.RemoteException;
}
