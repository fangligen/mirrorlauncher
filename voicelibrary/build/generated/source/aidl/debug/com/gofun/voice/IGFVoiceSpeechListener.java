/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\gofun\\GoFun_Launcher\\voicelibrary\\src\\main\\aidl\\com\\gofun\\voice\\IGFVoiceSpeechListener.aidl
 */
package com.gofun.voice;
// Declare any non-default types here with import statements

public interface IGFVoiceSpeechListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.gofun.voice.IGFVoiceSpeechListener
{
private static final java.lang.String DESCRIPTOR = "com.gofun.voice.IGFVoiceSpeechListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.gofun.voice.IGFVoiceSpeechListener interface,
 * generating a proxy if needed.
 */
public static com.gofun.voice.IGFVoiceSpeechListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.gofun.voice.IGFVoiceSpeechListener))) {
return ((com.gofun.voice.IGFVoiceSpeechListener)iin);
}
return new com.gofun.voice.IGFVoiceSpeechListener.Stub.Proxy(obj);
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
case TRANSACTION_onEndOfSpeech:
{
data.enforceInterface(descriptor);
this.onEndOfSpeech();
reply.writeNoException();
return true;
}
case TRANSACTION_onResult:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
this.onResult(_arg0);
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
case TRANSACTION_onSelect:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
this.onSelect(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onWakeupWord:
{
data.enforceInterface(descriptor);
this.onWakeupWord();
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
private static class Proxy implements com.gofun.voice.IGFVoiceSpeechListener
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
@Override public void onEndOfSpeech() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onEndOfSpeech, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onResult(java.lang.String jsonStr) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonStr);
mRemote.transact(Stub.TRANSACTION_onResult, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//声音

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
//选择第几页，支持1～6，最后一页，取消

@Override public void onSelect(int num, java.lang.String choose) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(num);
_data.writeString(choose);
mRemote.transact(Stub.TRANSACTION_onSelect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onWakeupWord() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onWakeupWord, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//-1 onResult==null -2 service_error

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
static final int TRANSACTION_onEndOfSpeech = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onVolumeChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_onSelect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_onWakeupWord = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public void onBeginOfSpeech() throws android.os.RemoteException;
public void onEndOfSpeech() throws android.os.RemoteException;
public void onResult(java.lang.String jsonStr) throws android.os.RemoteException;
//声音

public void onVolumeChanged(int volume) throws android.os.RemoteException;
//选择第几页，支持1～6，最后一页，取消

public void onSelect(int num, java.lang.String choose) throws android.os.RemoteException;
public void onWakeupWord() throws android.os.RemoteException;
//-1 onResult==null -2 service_error

public void onError(int code) throws android.os.RemoteException;
}
