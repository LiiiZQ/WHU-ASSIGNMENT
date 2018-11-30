DATA    SEGMENT
FL      DW      30  DUP(0)              ; 用两个数组储存结果
FH      DW      30  DUP(0)              
TMP     DW      ?                       ; 临时数组
DATA    ENDS


CODE	SEGMENT
	ASSUME  CS:CODE,ES:DATA,DS:DATA
MAIN	PROC	FAR                     ; 程序主体
START:	PUSH	DS
	SUB 	AX,AX
	PUSH	AX

        MOV     AX,DATA   
        MOV     DS,AX
        MOV     SI,OFFSET FL
        MOV     DI,OFFSET FH

        MOV     WORD PTR [SI],1         ; 第一个数：1
        MOV     WORD PTR [SI+2],1       ; 第二个数：1
        ADD     SI,4
        ADD     DI,4
        MOV     CX,28                   ; 循环28次

AGAIN:  MOV     AX,[SI-2]
        MOV     BX,[SI-4]
        ADD     AX,BX                   ; 计算下一个数(先加前半部分)
        MOV     [SI],AX

        MOV     AX,[DI-2]
        MOV     BX,[DI-4]
        ADC     AX,BX                   ; (加后半部分)
        MOV     [DI],AX
        
        ADD     SI,2
        ADD     DI,2
        LOOP    AGAIN

        MOV     CX,30
        MOV     SI,OFFSET FL
        MOV     DI,OFFSET FH
PRINT:  MOV     DX,[DI]
        MOV     AX,[SI]
        PUSH    CX                      ; protect CX
        CALL    PRINTER
        POP     CX                      ; recover CX
        ADD     SI,2
        ADD     DI,2
        LOOP    PRINT 

        RET
MAIN	ENDP



PRINTER PROC    NEAR                    ;转化为ASCII码表示打印
        MOV     CX,10000
        DIV     CX
        MOV     TMP,AX                  ; protect AX

        MOV     CX,4                    ; loop 4 times
        MOV     AX,DX
LOOP1:  MOV     DX,0
        MOV     BX,10
        DIV     BX                      ; AX = AX / 10, DX = AX % 10
        PUSH    DX
        LOOP    LOOP1        

        MOV     CX,2
        MOV     AX,TMP                  ; recover AX
LOOP2:  MOV     DX,0
        MOV     BX,10
        DIV     BX                      ; AX = AX / 10, DX = AX % 10
        PUSH    DX
        LOOP    LOOP2   


        MOV     CX,6                    ; 第30个斐波那契数832040(6位)
LOOP3:  POP     DX  
        OR      DL,30H                  ; print
        MOV     AH,2
        INT     21H
        LOOP    LOOP3


        MOV     DL,20H                  ; space
        MOV     AH,2
        INT     21H

        RET
PRINTER ENDP

CODE	ENDS
	END MAIN