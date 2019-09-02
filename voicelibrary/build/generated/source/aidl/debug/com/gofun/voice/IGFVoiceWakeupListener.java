/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\gofun\\GoFun_Launcher\\voicelibrary\\src\\main\\aidl\\com\\gofun\\voice\\IGFVoiceWakeupListener.aidl
 */
package com.gofun.voice;
// Declare any non-default types here with import statements

public interface IGFVoiceWakeupListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.gofun.voice.IGFVoiceWakeupListener
{
private static final java.lang.String DESCRIPTOR = "com.gofun.voice.IGFVoiceWakeupListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.gofun.voice.IGFVoiceWakeupListener interface,
 * generating a proxy if needed.
 */
public static com.gofun.voice.IGFVoiceWakeupListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.gofun.voice.IGFVoiceWakeupListener))) {
return ((com.gofun.voice.IGFVoiceWakeupListener)iin);
}
return new com.gofun.voice.IGFVoiceWakeupListener.Stub.Proxy(obj);
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
case TRANSACTION_onBeginOfSpeech:
{
data.enforceInterface(descriptor);
this.onBeginOfSpeech();
reply.writeNoException();
return true;
}
case TRANSACTION_onWakeup:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.onWakeup(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_onVolumeChanged:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
this.onVolumeChanged(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onError:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
this.onError(_arg0);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.gofun.voice.IGFVoiceWakeupListener
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
@Override public void onBeginOfSpeech() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onBeginOfSpeech, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onWakeup(java.lang.String sst, java.lang.String id, java.lang.String score) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(sst);
_data.writeString(id);
_data.writeString(score);
mRemote.transact(Stub.TRANSACTION_onWakeup, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onVolumeChanged(int volume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(volume);
mRemote.transact(Stub.TRANSACTION_onVolumeChanged, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onError(int code) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(code);
mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onBeginOfSpeech = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onWakeup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onVolumeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onBeginOfSpeech() throws android.os.RemoteException;
public void onWakeup(java.lang.String sst, java.lang.String id, java.lang.String score) throws android.os.RemoteException;
public void onVolumeChanged(int volume) throws android.os.RemoteException;
public void onError(int code) throws android.os.RemoteException;
}
