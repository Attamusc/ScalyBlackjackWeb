//Copyright (C) 2011 Ron Coleman. Contact: ronncoleman@gmail.com
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either
//version 3 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this library; if not, write to the Free Software
//Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
package code
package lib
package bj.hkeeping

case class Reply

/** General ok */
case object Ok extends Reply

/** General not ok */
case object NotOk extends Reply

/** Broke indicator */
case object Broke extends Reply


