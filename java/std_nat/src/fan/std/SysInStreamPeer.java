//
// Copyright (c) 2006, Brian Frank and Andy Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   2018-5-18 Jed Young Creation
//
package fan.std;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

import fan.sys.ByteArray;
import fan.sys.IOErr;

public class SysInStreamPeer {
	
	InputStream inputStream;


	private void init(InputStream in) {
		inputStream = in;
//		java.nio.charset.Charset jcharset = java.nio.charset.Charset.forName(c.name);
//		inputReader = new InputStreamReader(in, jcharset);
//		dataStream = new DataInputStream(in);
	}
	
	static SysInStreamPeer make(SysInStream self) {
		return new SysInStreamPeer();
	}
	
	public static InStream make(InputStream in, long bufSize) {
		return make(in, Endian.big, Charset.utf8, bufSize);
	}

	public static InStream make(InputStream in, Endian e, Charset c, long bufSize) {
		SysInStream sin = SysInStream.make(e, c);
		if (bufSize > 0) {
			in = new BufferedInputStream(in, (int) bufSize);
		}
		((SysInStreamPeer)sin.peer).init(in);
		return sin;
	}

	public long avail(SysInStream self) {
		try {
			return this.inputStream.available();
		} catch (IOException e) {
			throw IOErr.make(e);
		}
	}

	public long read(SysInStream self) {
		try {
			long res = this.inputStream.read();
//			if (res == -1) return FanInt.invalidVal;
			return res;
		} catch (IOException e) {
			throw IOErr.make(e);
		}
	}

	public long skip(SysInStream self, long n) {
		try {
			return this.inputStream.skip(n);
		} catch (IOException e) {
			throw IOErr.make(e);
		}
	}

	public long readBytes(SysInStream self, ByteArray ba, long off, long len) {
		try {
			return this.inputStream.read(ba.array(), (int)off, (int)len);
		} catch (IOException e) {
			throw IOErr.make(e);
		}
	}

	private void unreadF(int n) throws IOException {
		if (this.inputStream instanceof PushbackInputStream) {
			((PushbackInputStream) this.inputStream).unread(n);
		} else {
			PushbackInputStream p = new PushbackInputStream(this.inputStream, 128);
			this.init(p);
			((PushbackInputStream) this.inputStream).unread(n);
		}
	}

	public InStream unread(SysInStream self, long n) {
		try {
			unreadF((int) n);
			return self;
		} catch (IOException e) {
			throw IOErr.make(e);
		}
	}

	public boolean close(SysInStream self) {
		try {
			this.inputStream.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	//used by InStream
	public static long toSigned(long val, long num) {
		switch ((int)num) {
		case 1:
			return (byte)val;
		case 2:
			return (short)val;
		case 4:
			return (int)val;
		}
		return val;
	}
}
