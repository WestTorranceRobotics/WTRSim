using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEditor;

[InitializeOnLoad]
public static class Initializer
{
	static Initializer() {
		Debug.Log("Playmode Initialized!");
		EditorApplication.EnterPlaymode();
	}
	
}
