DATA    SEGMENT
PRIME   DB  100 DUP(?)  ; 储存1～100可能出现的质数
DATA    ENDS

CODE	SEGMENT
	ASSUME  CS:CODE,ES:DATA,DS:DATA

MAIN	PROC	FAR     ; 程序主体
START:	PUSH	DS
	SUB	AX,AX
	PUSH	AX

        MOV     AX,DATA
        MOV     DS,AX
        MOV     SI,OFFSET PRIME
        MOV     DI,OFFSET PRIME

        MOV     CH,1    ; 记录当前数字，2～100

LOOP:   INC     CH      
        CMP     CH,100  
        JA      PRINT   ; 检查完毕，进行打印
        MOV     CL,1    ; 寻找CH的因数，2~(CH/2)

FACTOR: INC     CL      ; 寻找因数，判断当前CH是否为质数
        MOV     BL,CH
        SHR     BL,1    
        CMP     CL,BL   
        JA      STORE   ; 是质数=>储存结果

        XOR     AX,AX   ; AX = 0
        MOV     AL,CH   ; AX = AL = CH, AH = 0
        DIV     CL      ; AH = CH % CL
        CMP     AH,0    ; 
        JE      LOOP    ; 不是质数=>检查下一个数字
        JMP     FACTOR

STORE:  MOV     [SI],CH
        INC     SI
        JMP     LOOP    ; 储存质数=>检查下一个数字

PRINT:  CMP     DI,SI
        JE      STOP    ; 打印完毕，结束

        SUB     AX,AX
        MOV     AL,[DI]
        CALL    PRINTER

        INC     DI
        JMP     PRINT   ; 打印下一个数字

STOP:   RET
MAIN	ENDP

PRINTER PROC    NEAR            ; 转化为ASCII码表示并打印
        MOV     BL,10
        DIV     BL              ; AH = AX % 10, AL = AX / 10
        XCHG    AH,AL           ; AL = AX % 10, AH = AX / 10
        OR      AX,3030H    
        MOV     CX,AX

        MOV     DL,CH           ; print 
        MOV     AH,2
        INT     21H
        MOV     DL,CL           ; print 
        MOV     AH,2
        INT     21H
        MOV     DL,20H          ; space
        MOV     AH,2
        INT     21H
        RET
PRINTER ENDP

CODE	ENDS
	END MAIN